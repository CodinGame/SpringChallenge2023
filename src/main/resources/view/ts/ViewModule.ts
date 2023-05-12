import { HEIGHT, WIDTH } from '../core/constants.js'
import { bell, ease, easeIn, easeOut } from '../core/transitions.js'
import { fitAspectRatio, lerp, lerpAngle, lerpPosition, unlerp, unlerpUnclamped } from '../core/utils.js'
import { parseData, parseGlobalData } from './Deserializer.js'
import { TooltipManager } from './TooltipManager.js'
import { ANT_HEIGHT, BACKGROUND, COLOR_NAMES, CONVEYOR_HEIGHT, CONVEYOR_WIDTH, HUD_HEIGHT, INDICATOR_OFFSET, TILE_HEIGHT } from './assetConstants.js'
import ev from './events.js'
import { EGG, POINTS } from './gameConstants.js'
import { hexToScreen, screenToHex } from './hex.js'
import { computePathSegments } from './pathSegments.js'
import { AnimData, AntParticle, AntParticleGroup, CanvasInfo, CellDto, ContainerConsumer, Effect, EventDto, Explosion, FrameData, FrameInfo, GlobalData, Hex, PlayerInfo, Tile } from './types.js'
import { angleDiff, fit, generateText, last, sum } from './utils.js'

const CONVEYOR_SCALE = 0.21
const MAX_PARTICLES_PER_GROUP = 8
const ARROW_FADE_OUT_P = 0.8
const XPLODE_PARTICLE_COUNT = 80
const XPLODE_DURATION = 1000
const THRESHOLD_TRIPLE = 50
const THRESHOLD_DOUBLE = 20
const EGG_COLORS = [0x01e31d, 0xb5b1b2]
const CRYSTAL_COLORS = [0xfdf100, 0xe86a02]

const MESSAGE_RECT = {
  x: 130,
  y: 80,
  w: 688,
  h: 42
}

interface EffectPool {
  [key: string]: Effect[]
}

const api = {
  options: {
    debugMode: true as boolean|number,
    seeAnts: false
  },
  setDebug: () => {},
  setSeeAnts: (v: boolean) => {}
}
export { api }

export class ViewModule {
  states: FrameData[]
  globalData: GlobalData
  pool: EffectPool
  playerSpeed: number
  previousData: FrameData
  currentData: FrameData
  progress: number
  oversampling: number
  container: PIXI.Container
  time: number
  tooltipManager: TooltipManager

  antParticleLayer: PIXI.Container
  boardOverlay: PIXI.Container
  boardLayer: PIXI.Container

  huds: {
    bar: PIXI.Sprite
    avatar: PIXI.Sprite
    ants: PIXI.Text
    nickname: PIXI.Text
    message: PIXI.Text
    score: PIXI.Text
    targetBarX: number
  }[]

  scoreBar: PIXI.Sprite

  hexes: Record<number, Hex>
  beacons: Record<number, PIXI.Sprite[]>
  currentTempCellData: { ants: number[][], beacons: number[][], richness: number[] }
  tiles: Record<number|string, Tile>

  particleGroupsByPlayer: {
    particleGroups: AntParticleGroup[]
    particleGroupByTile: Record<number, AntParticleGroup>
    particleGroupByMoveTo: Record<number, AntParticleGroup[]>
  }[]

  overlay: PIXI.Sprite
  conveyors: PIXI.TilingSprite[]
  conveyorLayer: PIXI.Container
  beaconLayer: PIXI.Container
  arrowLayer: PIXI.Container
  particleLayer: PIXI.Container
  counterLayer: PIXI.Container
  distanceBetweenHexes: number
  explosions: Explosion[]

  constructor () {
    this.states = []
    this.pool = {}
    this.tooltipManager = new TooltipManager()
    this.time = 0
    this.explosions = []

    window.debug = this
  }

  static get moduleName () {
    return 'graphics'
  }

  // Effects
  getFromPool<T extends PIXI.DisplayObject = PIXI.DisplayObject> (type: string): Effect<T> {
    if (!this.pool[type]) {
      this.pool[type] = []
    }

    for (const e of this.pool[type]) {
      if (!e.busy) {
        e.busy = true
        e.display.visible = true
        return e as Effect<T>
      }
    }

    const e = this.createEffect<T>(type)
    this.pool[type].push(e)
    e.busy = true
    return e as Effect<T>
  }

  createEffect<T extends PIXI.DisplayObject = PIXI.DisplayObject> (type: string): Effect<T> {
    let display = null
    if (type === 'conveyor') {
      display = PIXI.TilingSprite.from('convey3.png', { width: CONVEYOR_WIDTH, height: CONVEYOR_HEIGHT })
      display.interactive = true

      this.registerTooltip(display, () => {
        return (display as any).tooltip ?? ''
      })
      display.on('mouseover', () => (display as any).mouseOver?.())
      display.on('mouseout', () => (display as any).mouseOut?.())

      display.anchor.x = 0
      display.anchor.y = 0.5
      this.conveyors.push(display)
      this.conveyorLayer.addChild(display)
    } else if (type === 'antText') {
      display = generateText('5', 0xFFFFFF, 40, 2)
      this.counterLayer.addChild(display)
    } else if (type === 'arrow') {
      display = new PIXI.Container()
      const spriteContainer = new PIXI.Container()
      const sprite = PIXI.Sprite.from('Fleche_Bleu.png')
      const number = generateText('5', 0xFFFFFF, TILE_HEIGHT / 4, 0)
      sprite.anchor.set(0.5)
      number.anchor.set(0.5)
      spriteContainer.addChild(sprite)
      display.addChild(spriteContainer)
      display.addChild(number)
      fit(spriteContainer, Infinity, TILE_HEIGHT / 3)
      this.arrowLayer.addChild(display)
    } else if (type === 'particle') {
      display = PIXI.Sprite.from('particle.png')
      display.anchor.set(0.5)
      this.particleLayer.addChild(display)
    }
    return { busy: false, display }
  }

  updateScene (previousData: FrameData, currentData: FrameData, progress: number, playerSpeed?: number) {
    const lastShownData = this.currentData
    const frameChange = (lastShownData !== currentData)
    const fullProgressChange = ((this.progress === 1) !== (progress === 1))
    this.previousData = previousData
    this.currentData = currentData
    this.progress = progress
    this.playerSpeed = playerSpeed || 0

    // reset zIndex
    for (const [index, hex] of Object.entries(this.hexes)) {
      if (hex.icon != null) { hex.iconBounceContainer.zIndex = 1 }
      hex.indicatorLayer.zIndex = 2
    }

    this.resetEffects()
    this.updateTiles()
    this.updateBeacons()
    this.updateMoves()
    this.updateGains()
    this.updateExplosions()
    this.updateParticles(frameChange, lastShownData)
    this.updateHud()
    if (frameChange || (fullProgressChange && playerSpeed === 0)) {
      this.tooltipManager.updateGlobalText()
    }
  }

  destroyParticles () {
    if (this.antParticleLayer != null) {
      const children = [...this.antParticleLayer.children]
      this.antParticleLayer.removeChildren()
      children.forEach(c => c.destroy())
    }
    this.particleGroupsByPlayer = []
  }

  resetParticles () {
    this.destroyParticles()
    for (let playerIdx = 0; playerIdx < this.globalData.playerCount; ++playerIdx) {
      // Init particles
      const particleGroups: AntParticleGroup[] = []
      const particleGroupByTile: Record<number, AntParticleGroup> = {}
      const particleGroupByMoveTo: Record<string, AntParticleGroup[]> = {}
      for (const { index } of this.globalData.cells) {
        let stayingAnts = this.previousData.ants[playerIdx][index]

        this.currentData.events
          .filter(event => event.playerIdx === playerIdx)
          .filter(({ type }) => type === ev.MOVE)
          .filter(({ cellIdx }) => cellIdx === index)
          .forEach(event => {
            stayingAnts -= event.amount
          })
        if (stayingAnts > 0) {
          // A group of <stayingAnts> remains on this tile throughout the frame
          const particles = this.drawParticleGroup(stayingAnts, playerIdx)
          const group: AntParticleGroup = { particles, cellIdx: index, playerIdx }
          particleGroups.push(group)
          particleGroupByTile[index] = group
        }
      }
      this.currentData.events
        .filter(event => event.playerIdx === playerIdx)
        .filter(({ type }) => type === ev.MOVE)
        .forEach(event => {
          const particles = this.drawParticleGroup(event.amount, playerIdx)
          const group = { particles, fromIdx: event.cellIdx, toIdx: event.targetIdx, playerIdx, animData: event.animData }
          particleGroups.push(group)
          if (particleGroupByMoveTo[event.targetIdx] == null) {
            particleGroupByMoveTo[event.targetIdx] = []
          }
          particleGroupByMoveTo[event.targetIdx].push(group)
        })
      this.particleGroupsByPlayer.push({
        particleGroups,
        particleGroupByTile,
        particleGroupByMoveTo
      })
    }
  }

  updateParticles (frameChange: boolean, lastShownData: FrameData) {
    if (!api.options.seeAnts) {
      return
    }
    if (frameChange || this.particleGroupsByPlayer.length === 0) {
      if (lastShownData === this.previousData && this.particleGroupsByPlayer.length > 0) {
        // Use current particle groups

        const particleGroupsByPlayer = []
        for (let playerIdx = 0; playerIdx < this.globalData.playerCount; ++playerIdx) {
          const availableParticlesByCellIdx = {}
          for (const { index } of this.globalData.cells) {
            const currentGroupStayingOnTile = this.particleGroupsByPlayer[playerIdx].particleGroupByTile[index]
            const currentGroupMovedOntoTile = this.particleGroupsByPlayer[playerIdx].particleGroupByMoveTo[index]

            const availableParticles = (currentGroupStayingOnTile?.particles ?? [])
              .concat(currentGroupMovedOntoTile?.flatMap(group => group.particles) ?? [])
            availableParticlesByCellIdx[index] = availableParticles
          }

          // Redispatch particles
          // TODO: there is common code here with resetParticles, change that
          const particleGroups: AntParticleGroup[] = []
          const particleGroupByTile: Record<number, AntParticleGroup> = {}
          const particleGroupByMoveTo: Record<string, AntParticleGroup[]> = {}
          for (const { index } of this.globalData.cells) {
            let stayingAnts = this.previousData.ants[playerIdx][index]

            this.currentData.events
              .filter(event => event.playerIdx === playerIdx)
              .filter(({ type }) => type === ev.MOVE)
              .filter(({ cellIdx }) => cellIdx === index)
              .forEach(event => {
                stayingAnts -= event.amount
              })

            const arrivals = this.currentData.events
              .filter(event => event.playerIdx === playerIdx)
              .filter(({ type }) => type === ev.MOVE)
              .filter(({ targetIdx }) => targetIdx === index)
              .length

            const particleAmount = Math.min(arrivals === 0 ? MAX_PARTICLES_PER_GROUP * 2 : MAX_PARTICLES_PER_GROUP, stayingAnts)
            if (particleAmount > 0) {
              if (availableParticlesByCellIdx[index].length < particleAmount) {
                const newParticles = this.drawParticleGroup(particleAmount - availableParticlesByCellIdx[index].length, playerIdx)
                availableParticlesByCellIdx[index].push(...newParticles)
              }
              const particles = availableParticlesByCellIdx[index].splice(0, particleAmount)

              const group: AntParticleGroup = { particles, cellIdx: index, playerIdx }
              particleGroups.push(group)
              particleGroupByTile[index] = group
            }
          }
          this.currentData.events
            .filter(event => event.playerIdx === playerIdx)
            .filter(({ type }) => type === ev.MOVE)
            .forEach(event => {
              const particleAmount = Math.min(MAX_PARTICLES_PER_GROUP, event.amount)
              if (availableParticlesByCellIdx[event.cellIdx].length < particleAmount) {
                const newParticles = this.drawParticleGroup(particleAmount - availableParticlesByCellIdx[event.cellIdx].length, playerIdx)
                availableParticlesByCellIdx[event.cellIdx].push(...newParticles)
              }
              const particles = availableParticlesByCellIdx[event.cellIdx].splice(0, particleAmount)
              const group: AntParticleGroup = { particles, fromIdx: event.cellIdx, toIdx: event.targetIdx, playerIdx, animData: event.animData }
              particleGroups.push(group)
              if (particleGroupByMoveTo[event.targetIdx] == null) {
                particleGroupByMoveTo[event.targetIdx] = []
              }
              particleGroupByMoveTo[event.targetIdx].push(group)
            })
          particleGroupsByPlayer.push({
            particleGroups,
            particleGroupByTile,
            particleGroupByMoveTo
          })

          Object.values<AntParticle[]>(availableParticlesByCellIdx).flatMap(arr => arr).forEach(particle => {
            particle.sprite.parent.removeChild(particle.sprite)
            particle.sprite.destroy()
          })
        }

        this.particleGroupsByPlayer = particleGroupsByPlayer
      } else {
        this.resetParticles()
      }
    }
  }

  drawParticleGroup (amount: number, playerIdx: number): AntParticle[] {
    const particles: AntParticle[] = []
    for (let n = 0; n < Math.min(MAX_PARTICLES_PER_GROUP, amount); ++n) {
      const random = Math.random()
      const offset = { x: 0, y: 0 }
      const direction = Math.random() * Math.PI * 2

      const sprite = PIXI.Sprite.from(`Fourmie_${COLOR_NAMES[playerIdx]}.png`)
      sprite.scale.set((TILE_HEIGHT * 0.15) / ANT_HEIGHT)
      sprite.anchor.set(0.5)
      this.antParticleLayer.addChild(sprite)
      const antParticle = { sprite, random, offset, direction, placed: false }
      particles.push(antParticle)
    }
    return particles
  }

  showPlayerDebug (playerIdx: number) {
    return api.options.debugMode === true || api.options.debugMode === playerIdx
  }

  updateMoves () {
    const progress = this.progress
    for (const event of this.currentData.events.filter(({ type }) => type === ev.MOVE)) {
      if (!this.showPlayerDebug(event.playerIdx)) {
        continue
      }

      const p = this.getAnimProgress(event.animData, progress)
      if (p < 0 || p > 1) {
        continue
      }
      const fromIdx = event.cellIdx
      const toIdx = event.targetIdx
      const amount = event.amount
      const playerIdx = event.playerIdx

      const { display } = this.getFromPool<PIXI.Text>('arrow')
      const sprite = (display.children[0] as PIXI.Container).children[0] as PIXI.Sprite
      const number = display.children[1] as PIXI.Text
      number.text = amount.toString()
      sprite.texture = PIXI.Texture.from(`Fleche_${COLOR_NAMES[playerIdx]}.png`)

      const sourceCell = this.hexes[fromIdx]
      const targetCell = this.hexes[toIdx]
      const sourceP = hexToScreen(sourceCell.data.q, sourceCell.data.r)
      const targetP = hexToScreen(targetCell.data.q, targetCell.data.r)
      const newPosition = lerpPosition(sourceP, targetP, unlerp(0, 0.5, p) * 0.5)
      display.position.copyFrom(newPosition)
      const rotation = Math.atan2(targetP.y - sourceP.y, targetP.x - sourceP.x)
      sprite.rotation = rotation
      display.alpha = 1 - unlerp(ARROW_FADE_OUT_P, 1, p)

      if (event.double && api.options.debugMode === true) {
        let offset = TILE_HEIGHT / 8
        if (!event.crisscross && playerIdx === 0) {
          offset *= -1
        }
        display.position.x += Math.cos(rotation + Math.PI / 2) * offset
        display.position.y += Math.sin(rotation + Math.PI / 2) * offset
        display.scale.set(0.7)
      } else {
        display.scale.set(1)
      }

      sprite.scale.x = unlerp(0, 0.5, p)
      number.scale.set(unlerp(0, 0.25, p))
    }
  }

  updateExplosions () {
    const { currentData, previousData } = this
    for (const { cellIdx, data, start, end } of this.explosions) {
      let p = unlerpUnclamped(start, end, currentData.date + currentData.frameDuration * this.progress)

      if (p > 1 || p < 0) {
        continue
      }

      const targetCell = this.hexes[cellIdx]
      const targetPos = hexToScreen(targetCell.data.q, targetCell.data.r)
      const colors = targetCell.data.type === EGG ? EGG_COLORS : CRYSTAL_COLORS

      for (let i = 0; i < XPLODE_PARTICLE_COUNT; ++i) {
        const particle = this.getFromPool<PIXI.Sprite>('particle').display
        particle.tint = colors[i < XPLODE_PARTICLE_COUNT * 0.66 ? 0 : 1]
        const { angle, speed, size, deathAt } = data[i]
        particle.visible = true
        particle.angle = angle
        particle.position.x = targetPos.x + Math.cos(angle) * speed * easeOut(p)
        particle.position.y = targetPos.y + Math.sin(angle) * speed * easeOut(p)
        const scale = (TILE_HEIGHT / 18) / particle.texture.width
        particle.scale.set(scale * size * lerp(1, 0, easeIn(unlerp(0, deathAt, p))))
      }

      if (targetCell.data.type === EGG && currentData.richness[targetCell.data.index] <= 0) {
        let iconShrinkP = unlerp(0, 0.65, easeIn(p))
        const icon = targetCell.icon
        icon.visible = true

        if (targetCell.data.richness < THRESHOLD_DOUBLE) {
          icon.texture = PIXI.Texture.from('Oeufs_5.png')
        } else {
          icon.texture = PIXI.Texture.from('Oeufs_4.png')
        }
        icon.scale.set(1 - iconShrinkP)
      }
    }
  }

  updateGains () {
    const progress = this.progress

    for (const event of this.currentData.syntheticEvents) {
      const p = this.getAnimProgress(event.animData, progress)
      if (p < 0 || p > 1) {
        continue
      }
      if (!this.showPlayerDebug(event.playerIdx)) {
        continue
      }

      for (const cellIdx of event.bouncing) {
        const hex = this.hexes[cellIdx]
        hex.bouncing = true
        // swap zIndices
        if (hex.icon != null) { hex.iconBounceContainer.zIndex = 2 }
        hex.indicatorLayer.zIndex = 1
      }

      // Update +N counters
      for (const total of event.totals) {
        const counterP = unlerp(0, 0.6, p)
        const amount = total.amount
        const playerIdx = event.playerIdx

        const g = this.getFromPool<PIXI.Text>('antText')
        g.display.text = `+${amount}`
        g.display.tint = (event.type === ev.BUILD ? EGG_COLORS : CRYSTAL_COLORS)[0]

        const targetCell = this.hexes[total.cellIdx]
        const pos = hexToScreen(targetCell.data.q, targetCell.data.r)
        const targetP = { ...pos, y: pos.y - 100 / this.boardOverlay.scale.x }
        const newPosition = lerpPosition(pos, targetP, counterP)
        g.display.position.set(newPosition.x, newPosition.y)
        // Text size scales with map size
        g.display.scale.set(1 / this.boardOverlay.scale.x * lerp(1, 2.4, easeOut(p)))
        g.display.alpha = 1 - unlerp(0.5, 0.8, p)// - easeIn(p)
      }

      // Draw conveyors
      const playerColor = this.globalData.players[event.playerIdx].color
      const conveyorMap: Record<string, PIXI.TilingSprite> = {}
      for (const segment of event.segments) {
        const { display } = this.getFromPool<PIXI.TilingSprite>('conveyor')

        const fromCell = this.hexes[segment.from]
        const toCell = this.hexes[segment.to]
        const fromPos = hexToScreen(fromCell.data.q, fromCell.data.r)
        const toPos = hexToScreen(toCell.data.q, toCell.data.r)

        if (this.distanceBetweenHexes == null) {
          const dist = Math.sqrt(Math.pow(fromPos.x - toPos.x, 2) + Math.pow(fromPos.y - toPos.y, 2))
          this.distanceBetweenHexes = dist
        }
        const scaleMult = (TILE_HEIGHT * CONVEYOR_SCALE / CONVEYOR_HEIGHT)

        display.width = this.distanceBetweenHexes
        display.height = TILE_HEIGHT * CONVEYOR_SCALE
        display.tileScale.set(scaleMult)
        const rotation = Math.atan2(toPos.y - fromPos.y, toPos.x - fromPos.x)
        display.rotation = rotation
        display.position.copyFrom(fromPos)
        display.tint = playerColor
        display.alpha = this.upThenDown(p)
        display.y += CONVEYOR_HEIGHT * 2 * (event.playerIdx === 0 ? -1 : 1)

        conveyorMap[segment.key] = display
      }

      for (const segment of event.segments) {
        const conveyor = conveyorMap[segment.key]
        const attachedConveyors = [...segment.pathKeys].map(k => conveyorMap[k])

        if ((conveyor as any).mouseOver != null) {
          (conveyor as any).mouseOut()
          this.tooltipManager.clear() // TODO: less degueu
        }

        ;(conveyor as any).mouseOver = () => {
          attachedConveyors.forEach(c => { c.texture = PIXI.Texture.from('convey5.png'); c.tint = 0xFFFFFF })
        }

        ;(conveyor as any).mouseOut = () => {
          attachedConveyors.forEach(c => { c.texture = PIXI.Texture.from('convey3.png'); c.tint = playerColor })
        }

        ;(conveyor as any).tooltip = `Earned ${segment.amount} ${event.type === ev.FOOD ? 'point' : 'ant'}${segment.amount !== 1 ? 's' : ''}`
      }
    }
  }

  screenToBoard (point) {
    return {
      x: point.x + this.boardLayer.x,
      y: point.y + this.boardLayer.y
    }
  }

  getLastEventEndP (eventType: number): number {
    return this.currentData.events
      .filter(e => e.type === eventType)
      .map(e => e.animData.end)
      .reduce((a, b) => Math.max(a, b), 0)
  }

  getLastMoveEndP (): number {
    return this.getLastEventEndP(ev.MOVE)
  }

  getLastFoodEndP (): number {
    return this.getLastEventEndP(ev.FOOD)
  }

  getLastBuildEndP (): number {
    return this.getLastEventEndP(ev.BUILD)
  }

  getOwnersOfBeaconOn (cellIdx: number|string, data: FrameData) {
    const powers = this.globalData.players.map((_, playerIdx) => data.beacons[playerIdx][cellIdx])
    if (powers[0] > 0 && powers[1] > 0) {
      return [0, 1]
    }
    if (powers[0] === 0 && powers[1] > 0) {
      return [1]
    }
    if (powers[0] > 0 && powers[1] === 0) {
      return [0]
    }
    return []
  }

  updateBeacons () {
    for (const [index, hex] of Object.entries(this.hexes)) {
      const beacons = this.beacons[index]
      const beaconP = unlerp(0, 0.5, this.progress)

      beacons.map(beacon => {
        beacon.visible = false
        beacon.alpha = 1
      })

      const fromPlayers = this.getOwnersOfBeaconOn(index, this.previousData).filter(this.showPlayerDebug)
      const toPlayers = this.getOwnersOfBeaconOn(index, this.currentData).filter(this.showPlayerDebug)

      const fromSuffix = fromPlayers.length === 0 ? null : fromPlayers.length === 2 ? 'Rouge_Bleu' : COLOR_NAMES[fromPlayers[0]]
      const toSuffix = toPlayers.length === 0 ? null : toPlayers.length === 2 ? 'Rouge_Bleu' : COLOR_NAMES[toPlayers[0]]

      if (fromPlayers.length === 0 && toPlayers.length > 0) {
        const beacon = beacons[0]
        beacon.visible = true
        beacon.alpha = easeOut(beaconP)
        beacon.texture = PIXI.Texture.from(`Balise_${toSuffix}.png`)
      } else if (fromPlayers.length > 0 && toPlayers.length === 0) {
        const beacon = beacons[0]
        beacon.visible = true
        beacon.alpha = 1 - easeOut(beaconP)
        beacon.texture = PIXI.Texture.from(`Balise_${fromSuffix}.png`)
      } else if (fromPlayers.length > 0 && toPlayers.length > 0) {
        const beacon = beacons[0]
        beacon.visible = true
        beacon.texture = PIXI.Texture.from(`Balise_${fromSuffix}.png`)

        if (fromSuffix !== toSuffix) {
          const other = beacons[1]
          other.visible = true
          other.texture = PIXI.Texture.from(`Balise_${toSuffix}.png`)

          // Cross fade
          beacon.alpha = 1 - beaconP
          other.alpha = beaconP
        }
      }
    }
  }

  updateTiles () {
    for (const [index, hex] of Object.entries(this.hexes)) {
      const curr = this.currentData
      const prev = this.previousData
      this.tiles[index].sprite.alpha = 1
      this.tiles[index].sprite.tint = 0xFFFFFF
      this.hexes[index].bouncing = false

      for (const player of this.globalData.players) {
        const antAmountDataSource = this.progress >= (ARROW_FADE_OUT_P * this.getLastMoveEndP()) ? curr : prev
        const foodAmountDataSource = this.progress >= this.getLastFoodEndP() ? curr : prev
        const eggAmountDataSource = this.progress >= this.getLastBuildEndP() ? curr : prev

        const finalAmount = antAmountDataSource.ants[player.index][index]
        const buildAmount = curr.buildAmount[player.index][index]
        const isBetweenMoveAndBuild = this.progress >= (ARROW_FADE_OUT_P * this.getLastMoveEndP()) && this.progress < this.getLastBuildEndP()
        const temporaryAntAmount = isBetweenMoveAndBuild
          ? finalAmount - buildAmount
          : finalAmount
        hex.texts[player.index].visible = temporaryAntAmount > 0 && !api.options.seeAnts
        hex.texts[player.index].text = temporaryAntAmount.toString()

        hex.indicators[player.index].visible = temporaryAntAmount > 0 && !api.options.seeAnts

        const richness = (hex.data.type === EGG ? eggAmountDataSource : foodAmountDataSource).richness[index]
        let richIdx
        if (richness >= THRESHOLD_TRIPLE) {
          richIdx = 1
        } else if (richness >= THRESHOLD_DOUBLE) {
          richIdx = 2
        } else {
          richIdx = 3
        }

        if (richness > 0) {
          hex.foodText.text = richness.toString()
          hex.foodText.visible = true
          hex.foodTextBackground.visible = true
          if (hex.icon != null) {
            hex.icon.visible = true
            hex.icon.scale.set(1)
            if (hex.data.type === POINTS) {
              hex.icon.texture = PIXI.Texture.from(`Cristaux_${richIdx}.png`)
            } else {
              if (hex.data.richness < THRESHOLD_DOUBLE) {
                hex.icon.texture = PIXI.Texture.from('Oeufs.png')
              } else {
                hex.icon.texture = PIXI.Texture.from(`Oeufs_${richIdx}.png`)
              }
            }
          }
        } else if (hex.foodText != null) {
          hex.foodText.visible = false
          hex.foodTextBackground.visible = false
          if (hex.icon != null) {
            hex.icon.visible = false
          }
        }
      }
    }
  }

  updateHud () {
    const curr = this.currentData
    const prev = this.previousData
    const antAmountDataSource = this.progress >= this.getLastBuildEndP() ? curr : prev
    const scoreDataSource = this.progress >= this.getLastFoodEndP() ? curr : prev

    for (const player of this.globalData.players) {
      const hud = this.huds[player.index]
      hud.ants.text = antAmountDataSource.antTotals[player.index].toString()

      hud.score.text = scoreDataSource.scores[player.index].toString()
      hud.message.text = curr.messages[player.index]
      hud.message.scale.set(1)
      const place = (x) => player.index === 0 ? x : WIDTH - x
      fitTextWithin(hud.message, MESSAGE_RECT, place)

      const scoreRatio = scoreDataSource.scores[player.index] / this.globalData.maxScore
      const percentageOfWin = unlerp(0, 0.5, scoreRatio)
      const x = lerp(958 + (294 * (player.index === 0 ? -1 : 1)), 958, percentageOfWin)

      hud.targetBarX = x
    }
  }

  shake (entity: PIXI.DisplayObject, progress: number) {
    const shakeForceMax = 1.4
    const omega = 100000 * (Math.random() * 0.5 + 0.5)

    const shakeForce = shakeForceMax * unlerp(0, 0.5, bell(progress))
    const shakeX = shakeForce * Math.cos(2 * progress * omega)
    const shakeY = shakeForce * Math.sin(progress * omega)

    entity.pivot.x = shakeX
    entity.pivot.y = shakeY
  }

  toGlobal (element: PIXI.DisplayObject) {
    return this.container.toLocal(new PIXI.Point(0, 0), element)
  }

  getAnimProgress ({ start, end }: AnimData, progress: number) {
    return unlerpUnclamped(start, end, progress)
  }

  upThenDown (t) {
    return Math.min(1, bell(t) * 2)
  }

  resetEffects () {
    for (const type in this.pool) {
      for (const effect of this.pool[type]) {
        effect.display.visible = false
        effect.busy = false
      }
    }
  }

  animateRotation (sprite: PIXI.Sprite, rotation: number) {
    if (sprite.rotation !== rotation) {
      const eps = 0.02
      let r = lerpAngle(sprite.rotation, rotation, 0.133)
      if (angleDiff(r, rotation) < eps) {
        r = rotation
      }
      sprite.rotation = r
    }
  }

  animateScoreBar (delta: number) {
    for (const player of this.globalData.players) {
      const hud = this.huds[player.index]
      const bar = hud.bar
      bar.x = lerp(bar.x, hud.targetBarX, 0.06)
    }
  }

  animateScene (delta) {
    this.time += delta

    this.animateParticleGroups(delta)
    this.animateConveyors(delta)
    this.animateTiles(delta)
    this.animateScoreBar(delta)
  }

  farFromOne (x: number) {
    const eps = 0.025
    const xabs = Math.abs(x)
    return (xabs < (1 - eps) || xabs > (1 + eps))
  }

  animateTiles (delta: number) {
    for (const hex of Object.values(this.hexes)) {
      if (hex.icon != null) {
        const iconBounceContainer = hex.iconBounceContainer
        if (hex.bouncing || this.farFromOne(iconBounceContainer.scale.x) || this.farFromOne(iconBounceContainer.scale.y)) {
          const relTime = this.time / 160
          var cos = Math.cos(relTime)
          var factor = 0.1
          iconBounceContainer.scale.x = cos * factor + 1
          iconBounceContainer.scale.y = -cos * factor + 1
          hex.foodText.pivot.y = cos * 10
          hex.foodTextBackground.pivot.y = cos * 10
        } else {
          iconBounceContainer.scale.set(1)
          hex.foodText.pivot.y = 0
          hex.foodTextBackground.pivot.y = 0
        }
      }
    }
  }

  animateParticleGroups (delta: number) {
    if (!api.options.seeAnts) {
      return
    }
    for (let playerIdx = 0; playerIdx < this.globalData.playerCount; ++playerIdx) {
      if (this.particleGroupsByPlayer[playerIdx] == null) {
        continue
      }
      for (const antParticleGroup of this.particleGroupsByPlayer[playerIdx].particleGroups) {
        let failsafeTriggered = false
        const { particles, cellIdx, fromIdx, toIdx, animData } = antParticleGroup
        if (cellIdx != null) {
          // Staying on cell
          failsafeTriggered = this.animateAntParticles(cellIdx, cellIdx, particles, 0)
        } else {
          // Moving to neighbouring cell
          const moveP = Math.min(1, Math.max(0, this.getAnimProgress(animData, this.progress)))

          failsafeTriggered = this.animateAntParticles(fromIdx, toIdx, particles, ease(moveP))
        }
        if (failsafeTriggered) {
          return
        }
      }
    }
  }

  animateConveyors (delta: number) {
    for (const ts of this.conveyors) {
      if (ts.parent == null) {
        // Failsafe
        return
      }

      ts.tilePosition.x = this.time * ts.tileScale.x / 16
    }
  }

  animateAntParticles (fromIdx: number, toIdx: number, particles: AntParticle[], progress: number) {
    const speed = 2
    const hexFrom = this.hexes[fromIdx]
    const hexTo = this.hexes[toIdx]
    const maxOffset = lerp(TILE_HEIGHT / 10, TILE_HEIGHT / 4, (1 - bell(progress)))

    for (const particle of particles) {
      if ((particle as any).sprite._destroyed) {
        this.destroyParticles()
        return true
      }

      const posFrom = hexToScreen(hexFrom.data.q, hexFrom.data.r)
      const posTo = hexToScreen(hexTo.data.q, hexTo.data.r)
      const pos = lerpPosition(posFrom, posTo, progress)

      pos.x += particle.offset.x
      pos.y += particle.offset.y

      const rotation = Math.atan2(pos.y - particle.sprite.y, pos.x - particle.sprite.x) + Math.PI / 2
      particle.sprite.rotation = lerpAngle(particle.sprite.rotation, rotation, 0.2)
      particle.sprite.position.copyFrom(particle.placed ? lerpPosition(particle.sprite.position, pos, 0.4) : pos)
      particle.placed = true

      const nextOffset = {
        x: speed * Math.cos(particle.direction) + particle.offset.x,
        y: speed * Math.sin(particle.direction) + particle.offset.y
      }

      if (nextOffset.x > maxOffset || nextOffset.x < -maxOffset || nextOffset.y > maxOffset || nextOffset.y < -maxOffset) {
        particle.direction += Math.PI
        particle.direction += (Math.random() * Math.PI / 4) - Math.PI / 2
        particle.offset.x = particle.offset.x > maxOffset ? maxOffset : particle.offset.x
        particle.offset.x = particle.offset.x < -maxOffset ? -maxOffset : particle.offset.x
        particle.offset.y = particle.offset.y > maxOffset ? maxOffset : particle.offset.y
        particle.offset.y = particle.offset.y < -maxOffset ? -maxOffset : particle.offset.y
      } else {
        particle.offset = nextOffset
      }
    }
    return false
  }

  asLayer (func: ContainerConsumer): PIXI.Container {
    const layer = new PIXI.Container()
    func.bind(this)(layer)
    return layer
  }

  drawTile (cell: CellDto) {
    const sprite = PIXI.Sprite.from('Case.png')
    sprite.anchor.set(0.5)

    return sprite
  }

  initBackground (layer: PIXI.Container) {
    const b = PIXI.Sprite.from(BACKGROUND)
    fit(b, Infinity, HEIGHT)
    layer.addChild(b)
  }

  initBeacons (layer: PIXI.Container) {
    this.beacons = {}
    for (const cell of this.globalData.cells) {
      const beacons: PIXI.Sprite[] = []
      for (const player of this.globalData.players) {
        const crosshair = PIXI.Sprite.from(`Balise_${player.index === 0 ? 'Bleu' : 'Rouge'}.png`)
        crosshair.anchor.set(0.5)

        const hexaP = hexToScreen(cell.q, cell.r)
        crosshair.position.set(hexaP.x, hexaP.y)

        beacons.push(crosshair)
        layer.addChild(crosshair)
      }
      this.beacons[cell.index] = beacons
    }
    this.centerLayer(layer)
  }

  centerLayer (layer: PIXI.Container) {
    layer.position.set(WIDTH / 2, (HEIGHT + HUD_HEIGHT) / 2)
  }

  initBoard (layer: PIXI.Container) {
    this.tiles = {}
    for (const cell of this.globalData.cells) {
      const hex = this.initHex(cell)
      layer.addChild(hex)
    }
    this.centerLayer(layer)
  }

  initTileData (layer: PIXI.Container) {
    this.hexes = {}
    for (const cell of this.globalData.cells) {
      const hex = this.initHexData(cell)
      layer.addChild(hex)
    }
    this.centerLayer(layer)
  }

  initHex (cell: CellDto) {
    const drawnHex = this.drawTile(cell)

    const container = new PIXI.Container()
    container.addChild(drawnHex)
    const hexaP = hexToScreen(cell.q, cell.r)
    container.position.set(hexaP.x, hexaP.y)
    this.tiles[cell.index] = {
      sprite: drawnHex,
      container
    }
    return container
  }

  initHexData (cell: CellDto) {
    const container = new PIXI.Container()
    container.sortableChildren = true

    const hexaP = hexToScreen(cell.q, cell.r)
    container.position.set(hexaP.x, hexaP.y)
    if (cell.owner !== -1) {
      const anthill = PIXI.Sprite.from(`Fourmiliere_${COLOR_NAMES[cell.owner]}.png`)
      anthill.anchor.set(0.5)
      container.addChild(anthill)
      anthill.zIndex = 0
    }
    let foodTextBackground: PIXI.Sprite = null
    let foodText: PIXI.Text = null
    let icon: PIXI.Sprite = null
    let iconBounceContainer: PIXI.Container = null

    if (cell.richness > 0) {
      iconBounceContainer = new PIXI.Container()
      foodTextBackground = PIXI.Sprite.from('Oeufs_Nombre.png')
      foodTextBackground.anchor.set(0.5)
      foodText = generateText(cell.richness, 0x0, TILE_HEIGHT / 6)

      fit(foodTextBackground, Infinity, TILE_HEIGHT / 6)

      if (cell.type === 1) {
        icon = PIXI.Sprite.from('Oeufs_1.png')
        icon.anchor.set(0.5)
        iconBounceContainer.addChild(icon)
        container.addChild(iconBounceContainer)
        iconBounceContainer.zIndex = 1
      } else {
        icon = PIXI.Sprite.from('Cristaux_1.png')
        icon.anchor.set(0.5)
        iconBounceContainer.addChild(icon)
        container.addChild(iconBounceContainer)
        iconBounceContainer.zIndex = 1
      }
      container.addChild(foodTextBackground)
      container.addChild(foodText)
      foodTextBackground.zIndex = 2
      foodText.zIndex = 3
    }

    const indicators: PIXI.Sprite[] = []
    const texts: PIXI.Text[] = []
    const indicatorLayer = new PIXI.Container()
    for (const player of this.globalData.players) {
      const offsetY = INDICATOR_OFFSET
      const indicator = PIXI.Sprite.from(`Fourmies_Nombre_${COLOR_NAMES[player.index]}.png`)
      indicator.anchor.set(0.5, player.index === 0 ? 0 : 1)
      // indicator.position.set(hexaP.x, hexaP.y + (player.index === 0 ? -offsetY : offsetY))
      indicator.position.set(0, player.index === 0 ? -offsetY : offsetY)
      indicators.push(indicator)
      indicatorLayer.addChild(indicator)
      indicator.scale.set(1.4)

      const text = generateText('0', 0xFFFFFF, indicator.height * 0.8)
      text.anchor.set(0.5, 0.5)
      text.position.set(0, indicator.y + (indicator.height / 2) * (player.index === 0 ? 1 : -1))
      texts.push(text)
      indicatorLayer.addChild(text)
    }
    container.addChild(indicatorLayer)

    this.hexes[cell.index] = {
      container: container,
      data: cell,
      texts,
      indicators,
      foodText,
      foodTextBackground,
      icon,
      iconBounceContainer,
      bouncing: false,
      indicatorLayer
    }

    return container
  }

  initHud (layer: PIXI.Container) {
    this.huds = []
    const hudFrame = PIXI.Sprite.from('HUD.png')

    const backdrop = new PIXI.Sprite(PIXI.Texture.WHITE)
    backdrop.anchor.set(0, 0.5)
    backdrop.tint = 0x454142
    backdrop.position.set(0, 51)
    backdrop.width = WIDTH
    backdrop.height = 96

    const barLayer = new PIXI.Container()

    layer.addChild(backdrop)
    layer.addChild(barLayer)
    layer.addChild(hudFrame)

    // this.scoreBar = bar

    for (const player of this.globalData.players) {
      const place = (x) => player.index === 0 ? x : WIDTH - x

      const avatar = new PIXI.Sprite(player.avatar)
      avatar.position.set(place(51), 51)
      avatar.width = 96
      avatar.height = 96
      avatar.anchor.set(0.5)

      const ANT_RECT = {
        x: 503,
        y: 11,
        w: 100,
        h: 48
      }
      const ants = generateText('10', 0xFFFFFF, 54, 0)
      fitTextWithin(ants, ANT_RECT, place)

      const NICKNAME_RECT = {
        x: 115,
        y: 13,
        w: 354,
        h: 43
      }
      const nickname = generateText(player.name, 0xFFFFFF, 54, 0)
      fitTextWithin(nickname, NICKNAME_RECT, place)

      const message = generateText('Chat zone', player.color, 64, 0)
      fitTextWithin(message, MESSAGE_RECT, place)

      const SCORE_RECT = {
        x: 672,
        y: 20,
        w: 60,
        h: 29
      }
      const score = generateText('000', 0xFFFFFF, 64, 0)
      fitTextWithin(score, SCORE_RECT, place)

      const bar = PIXI.Sprite.from(`Jauge_${COLOR_NAMES[player.index]}.png`)
      bar.anchor.set(player.index === 0 ? 1 : 0, 0.5)
      const barX = 958 + (294 * (player.index === 0 ? -1 : 1))
      bar.position.set(barX, 34)

      this.huds.push({
        avatar,
        ants,
        nickname,
        message,
        score,
        bar,
        targetBarX: barX
      })

      const playerHud = new PIXI.Container()

      barLayer.addChild(bar)
      playerHud.addChild(avatar)
      playerHud.addChild(ants)
      playerHud.addChild(nickname)
      playerHud.addChild(message)
      playerHud.addChild(score)
      layer.addChild(playerHud)
    }
  }

  reinitScene (container: PIXI.Container, canvasData: CanvasInfo) {
    this.oversampling = canvasData.oversampling
    this.container = container
    this.pool = {}
    this.conveyors = []
    this.distanceBetweenHexes = null

    this.destroyParticles()

    const tooltipLayer = this.tooltipManager.reinit()

    this.antParticleLayer = new PIXI.Container()
    this.centerLayer(this.antParticleLayer)

    this.particleLayer = new PIXI.Container()
    this.centerLayer(this.particleLayer)

    this.counterLayer = new PIXI.Container()
    this.centerLayer(this.counterLayer)

    const background = this.asLayer(this.initBackground)
    this.boardLayer = this.asLayer(this.initBoard)

    this.beaconLayer = this.asLayer(this.initBeacons)
    this.arrowLayer = new PIXI.Container()
    this.conveyorLayer = new PIXI.Container()

    const hudLayer = this.asLayer(this.initHud)

    this.boardOverlay = this.asLayer(this.initTileData)
    this.boardOverlay.addChild(this.conveyorLayer)
    this.boardOverlay.addChild(this.arrowLayer)

    const gameZone = new PIXI.Container()
    gameZone.addChild(this.boardLayer)
    gameZone.addChild(this.beaconLayer)
    gameZone.addChild(this.antParticleLayer)
    gameZone.addChild(this.boardOverlay)
    gameZone.addChild(this.particleLayer)
    gameZone.addChild(this.counterLayer)

    container.addChild(background)
    container.addChild(gameZone)
    container.addChild(hudLayer)
    container.addChild(tooltipLayer)

    const pad = 20
    fit(this.boardLayer, WIDTH - pad, HEIGHT - pad - HUD_HEIGHT)
    this.antParticleLayer.scale.copyFrom(this.boardLayer.scale)
    this.beaconLayer.scale.copyFrom(this.boardLayer.scale)
    this.particleLayer.scale.copyFrom(this.boardLayer.scale)
    this.boardOverlay.scale.copyFrom(this.boardLayer.scale)
    this.counterLayer.scale.copyFrom(this.boardLayer.scale)

    container.interactive = true

    tooltipLayer.interactiveChildren = false
    hudLayer.interactiveChildren = false

    this.tooltipManager.registerGlobal(data => {
      const point = data.getLocalPosition(this.boardLayer)
      const hex = screenToHex(point)
      const cell = this.globalData.cells.find(cell => cell.q === hex.q && cell.r === hex.r)
      if (cell == null) {
        return null
      }
      const { ants, beacons, richness, consumedFrom } = this.currentData
      const lines = [`Cell index: ${cell.index}`]
      if (ants[0][cell.index] > 0) {
        lines.push(`Blue ants: ${ants[0][cell.index]}`)
      }
      if (ants[1][cell.index] > 0) {
        lines.push(`Red ants: ${ants[1][cell.index]}`)
      }
      if (beacons[0][cell.index] > 0) {
        lines.push(`Blue beacon: ${beacons[0][cell.index]}`)
      }
      if (beacons[1][cell.index] > 0) {
        lines.push(`Red beacon: ${beacons[1][cell.index]}`)
      }
      if (cell.type === EGG && richness[cell.index] > 0) {
        lines.push(`${richness[cell.index]} eggs`)
      }
      if (cell.type === POINTS && richness[cell.index] > 0) {
        lines.push(`${richness[cell.index]} crystals`)
      }
      if (consumedFrom[0].has(cell.index) && consumedFrom[1].has(cell.index)) {
        lines.push('Both players harvest here')
      } else if (consumedFrom[0].has(cell.index)) {
        lines.push('Blue harvests here')
      } else if (consumedFrom[1].has(cell.index)) {
        lines.push('Red harvests here')
      }

      return lines.join('\n')
    })

    container.on('mousemove', (event) => {
      this.tooltipManager.moveTooltip(event)
    })

    api.setSeeAnts = (enabled) => {
      if (!enabled) {
        this.destroyParticles()
      } else {
        this.resetParticles()
      }
      if (this.previousData != null && this.progress != null) {
        this.updateScene(this.previousData, this.currentData, this.progress, this.playerSpeed)
      }
    }

    api.setDebug = () => {
      if (this.previousData != null && this.progress != null) {
        this.updateScene(this.previousData, this.currentData, this.progress, this.playerSpeed)
      }
    }
  }

  registerTooltip (container: PIXI.Container, getString: () => string) {
    container.interactive = true
    this.tooltipManager.register(container, getString)
  }

  drawDebugFrameAroundObject (o, col = 0xFF00FF, ancX, ancY) {
    const frame = new PIXI.Graphics()
    const x = -o.width * (o.anchor?.x ?? ancX)
    const y = -o.height * (o.anchor?.y ?? ancY)
    frame.beginFill(col, 1)
    frame.drawRect(x, y, o.width, o.height)
    frame.position.copyFrom(o)
    return frame
  }

  handleGlobalData (players: PlayerInfo[], raw: string): void {
    const globalData = parseGlobalData(raw)

    const anthills: number[][] = [[], []]
    globalData.cells.filter(cell => cell.owner !== -1).forEach(cell => anthills[cell.owner].push(cell.index))

    const maxScore = globalData.cells
      .filter(cell => cell.type === POINTS)
      .map(cell => cell.richness)
      .reduce((a, b) => a + b, 0)

    this.globalData = {
      ...globalData,
      players: players,
      playerCount: players.length,
      anthills,
      maxScore
    }

    this.currentTempCellData = {
      ants: this.globalData.players.map(p => this.globalData.cells.map(cell => cell.ants[p.index])),
      beacons: this.globalData.players.map(p => this.globalData.cells.map(() => 0)),
      richness: this.globalData.cells.map(cell => cell.richness)
    }
  }

  createExplosionParticleEffect (xplosion: Explosion): Explosion {
    for (let p = 0; p < XPLODE_PARTICLE_COUNT; ++p) {
      const angle = Math.random() * Math.PI * 2
      const speed = lerp(220, 500, Math.random())
      const size = lerp(1.3, 1.5, Math.random())
      const deathAt = lerp(0.8, 1, Math.random())
      xplosion.data.push({
        angle,
        speed,
        size,
        deathAt
      })
    }
    return xplosion
  }

  handleFrameData (frameInfo: FrameInfo, raw: string): FrameData {
    const dto = parseData(raw, this.globalData)

    const ants: number[][] = this.currentTempCellData.ants.map(arr => [...arr])
    const richness: number[] = [...this.currentTempCellData.richness]
    const eventMapPerPlayer: Record<number, EventDto[]>[] = [{}, {}]

    const antTotals: number[] = [
      sum(ants[0]),
      sum(ants[1])
    ]

    const consumedFrom = [new Set<number>(), new Set<number>()]

    // Handle explosions
    const shouldExplodeThisFrame = new Set<Explosion>()

    for (const event of dto.events) {
      eventMapPerPlayer[event.playerIdx][event.type] = eventMapPerPlayer[event.playerIdx][event.type] ?? []
      eventMapPerPlayer[event.playerIdx][event.type].push(event)
      const pStart = event.animData.start / frameInfo.frameDuration
      const pEnd = event.animData.end / frameInfo.frameDuration

      if (event.type === ev.BUILD) {
        consumedFrom[event.playerIdx].add(event.path[0])
        for (const cellIdx of this.globalData.anthills[event.playerIdx]) {
          ants[event.playerIdx][cellIdx] += event.amount
        }
        const fromRichness = richness[event.path[0]]
        const toRichness = fromRichness - event.amount

        if (fromRichness >= THRESHOLD_TRIPLE && toRichness < THRESHOLD_TRIPLE ||
          fromRichness >= THRESHOLD_DOUBLE && toRichness < THRESHOLD_DOUBLE ||
          toRichness === 0) {
          const tStart = frameInfo.date + frameInfo.frameDuration * pEnd
          const tEnd = tStart + XPLODE_DURATION
          const e: Explosion = {
            cellIdx: event.path[0],
            start: tStart,
            end: tEnd,
            data: []
          }

          shouldExplodeThisFrame.add(e)
        }

        richness[event.path[0]] = toRichness
      } else if (event.type === ev.MOVE) {
        ants[event.playerIdx][event.cellIdx] -= event.amount
        ants[event.playerIdx][event.targetIdx] += event.amount
        for (const other of dto.events) {
          if (other.cellIdx === event.targetIdx && other.targetIdx === event.cellIdx) {
            event.double = true
            event.crisscross = true
          } else if (other.cellIdx === event.cellIdx && other.targetIdx === event.targetIdx && other.playerIdx !== event.playerIdx) {
            event.double = true
            event.crisscross = false
          }
        }
      } else if (event.type === ev.FOOD) {
        consumedFrom[event.playerIdx].add(event.path[0])
        const fromRichness = richness[event.path[0]]
        const toRichness = fromRichness - event.amount
        if (fromRichness >= THRESHOLD_TRIPLE && toRichness < THRESHOLD_TRIPLE ||
          fromRichness >= THRESHOLD_DOUBLE && toRichness < THRESHOLD_DOUBLE ||
          toRichness === 0) {
          const tStart = frameInfo.date + frameInfo.frameDuration * pEnd
          const tEnd = tStart + XPLODE_DURATION
          const e: Explosion = {
            cellIdx: event.path[0],
            start: tStart,
            end: tEnd,
            data: []
          }

          shouldExplodeThisFrame.add(e)
        }

        richness[event.path[0]] = toRichness
      }
      event.animData.start /= frameInfo.frameDuration
      event.animData.end /= frameInfo.frameDuration
    }

    // Create synthetic multi-path event to aggregate FOOD and BUILD events
    const foodEvents = this.globalData.players.map(p => computePathSegments(eventMapPerPlayer[p.index][ev.FOOD] ?? [], p.index, ev.FOOD))
    const buildEvents = this.globalData.players.map(p => computePathSegments(eventMapPerPlayer[p.index][ev.BUILD] ?? [], p.index, ev.BUILD))

    const buildAmount = ants
      .map((playerAnts, playerIndex) => {
        return playerAnts.map((_, cellIndex) => {
          return dto.events
            .filter(event => event.type === ev.BUILD)
            .filter(event => event.playerIdx === playerIndex)
            .filter(event => last(event.path) === cellIndex)
            .reduce((buildAmount, event) => buildAmount + event.amount, 0)
        })
      })

    this.explosions.push(...[...shouldExplodeThisFrame].map(e => this.createExplosionParticleEffect(e)))

    const frameData: FrameData = {
      ...dto,
      ...frameInfo,
      syntheticEvents: [...foodEvents, ...buildEvents].filter(v => v != null),
      previous: null,
      buildAmount,
      ants,
      richness,
      antTotals,
      consumedFrom
    }
    frameData.previous = last(this.states) ?? frameData

    this.states.push(frameData)
    this.currentTempCellData = { ...frameData }
    return frameData
  }
}

function fitTextWithin (text: PIXI.Text, RECT: { x: number, y: number, w: number, h: number }, place: (x: number) => number) {
  text.anchor.set(0.5)
  const x = RECT.x + RECT.w / 2
  const y = RECT.y + RECT.h / 2
  text.position.set(place(x), y)
  if (text.width > RECT.w || text.height > RECT.h) {
    const coeff = fitAspectRatio(text.width, text.height, RECT.w, RECT.h)
    text.scale.set(coeff)
  }
}
