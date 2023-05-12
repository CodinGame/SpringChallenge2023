import { IPoint, IPointData } from 'pixi.js'

export type ContainerConsumer = (layer: PIXI.Container) => void

/**
 * Given by the SDK
 */
export interface FrameInfo {
  number: number
  frameDuration: number
  date: number
}
/**
 * Given by the SDK
 */
export interface CanvasInfo {
  width: number
  height: number
  oversampling: number
}
/**
 * Given by the SDK
 */
export interface PlayerInfo {
  name: string
  avatar: PIXI.Texture
  color: number
  index: number
  isMe: boolean
  number: number
  type?: string
}

export interface EventDto {
  type: number
  animData: AnimData
  cellIdx: number
  targetIdx: number
  amount: number
  playerIdx: number
  path: number[]

  /* Generated locally */
  double?: boolean
  crisscross?: boolean
}

export interface FrameDataDto {
  scores: number[]
  events: EventDto[]
  messages: string[]
  beacons: number[][]
}

export interface PathSegment {
  key: string
  from: number
  to: number
  amount: number
  pathKeys: Set<string>
}

export interface AggregatedPathsEvent {
  type: number
  animData: AnimData
  segments: PathSegment[]
  segmentMap: Record<string, PathSegment>
  totals: {cellIdx: number, amount: number}[]
  playerIdx: number
  bouncing: number[]
}

export interface FrameData extends FrameDataDto, FrameInfo {
  number: number
  previous: FrameData
  ants: number[][]
  richness: number[]
  syntheticEvents: AggregatedPathsEvent[]
  buildAmount: number[][]
  antTotals: number[]
  consumedFrom: Set<number>[]
}

export interface CoordDto {
  x: number
  y: number
}

export interface CellDto {
  q: number
  r: number
  richness: number
  index: number
  owner: number
  type: number
  ants: number[]
}

export interface GlobalDataDto {
  cells: CellDto[]
}
export interface GlobalData extends GlobalDataDto {
  players: PlayerInfo[]
  playerCount: number
  anthills: number[][]
  maxScore: number
}

export interface AnimData {
  start: number
  end: number
}

export interface Effect<T extends PIXI.DisplayObject = PIXI.DisplayObject> {
  busy: boolean
  display: T
}

/* View entities */
export interface Hex {
  container: PIXI.Container
  data: CellDto
  texts: PIXI.Text[]
  indicators: PIXI.Sprite[]
  foodText: PIXI.Text | null
  foodTextBackground: PIXI.Sprite | null
  icon: PIXI.Sprite | null
  iconBounceContainer: PIXI.Container | null
  bouncing: boolean
  indicatorLayer: PIXI.Container
}
export interface Tile {
  container: PIXI.Container
  sprite: PIXI.Sprite
}

export interface AntParticleGroup {
  particles: AntParticle[]
  fromIdx?: number
  toIdx?: number
  cellIdx?: number
  playerIdx: number
  animData?: AnimData
}

export interface AntParticle {
  offset: IPointData
  random: number
  direction: number
  sprite: PIXI.Sprite
  placed: boolean
}

export interface SfxData {
  angle: number
  speed: number
  size: number
  deathAt: number
}

export interface Explosion extends AnimData {
  cellIdx: number
  data: SfxData[]
}
