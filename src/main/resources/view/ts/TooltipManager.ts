import { HEIGHT } from '../core/constants.js'

/* global PIXI */

const PADDING = 5
const CURSOR_WIDTH = 20

function generateText (text, size, color, align): PIXI.Text {
  var textEl = new PIXI.Text(text, {
    fontSize: Math.round(size / 1.2) + 'px',
    fontFamily: 'Lato',
    fontWeight: 'bold',
    fill: color,
    lineHeight: size
  })

  if (align === 'right') {
    textEl.anchor.x = 1
  } else if (align === 'center') {
    textEl.anchor.x = 0.5
  }

  return textEl
};

export class TooltipManager {
  tooltip: PIXI.Container
  tooltipContainer: PIXI.Container
  tooltipLabel: PIXI.Text
  tooltipBackground: PIXI.Graphics
  tooltipOffset: number
  registry: {element: PIXI.DisplayObject, getText: () => string}[]
  inside: {[id: number]: boolean}
  getGlobalText: (data: PIXI.InteractionData) => string
  lastEvent: PIXI.InteractionEvent

  reinit () {
    const container = new PIXI.Container()
    const tooltip = new PIXI.Container()
    const background = new PIXI.Graphics()
    const label = generateText('DEFAULT', 36, 0xFFFFFF, 'left')

    label.position.x = PADDING
    label.position.y = PADDING

    tooltip.visible = false
    tooltip.addChild(background)
    tooltip.addChild(label)
    this.tooltipBackground = background
    this.tooltipLabel = label
    this.tooltipContainer = container
    this.tooltip = tooltip
    this.registry = []
    this.inside = {}
    this.tooltipOffset = 0
    this.getGlobalText = null

    container.addChild(this.tooltip)
    return container
  }

  clear () {
    this.inside = {}
  }

  registerGlobal (getText: (data: PIXI.InteractionData) => string) {
    this.getGlobalText = getText
  }

  register (element: PIXI.DisplayObject, getText: () => string) {
    const registryIdx = this.registry.length
    this.registry.push({ element, getText })
    element.on('mouseover', () => {
      this.inside[registryIdx] = true
    })

    element.on('mouseout', () => {
      delete this.inside[registryIdx]
    })
  }

  showTooltip (text: string) {
    this.setTooltipText(this.tooltip, text)
  }

  setTooltipText (tooltip: PIXI.Container, text: string) {
    this.tooltipLabel.text = text

    const width = this.tooltipLabel.width + PADDING * 2
    const height = this.tooltipLabel.height + PADDING * 2

    this.tooltipOffset = -width

    this.tooltipBackground.clear()
    this.tooltipBackground.beginFill(0x0, 0.9)
    this.tooltipBackground.drawRect(0, 0, width, height)
    this.tooltipBackground.endFill()

    tooltip.visible = true
  }

  updateGlobalText () {
    if (this.lastEvent != null) {
      this.moveTooltip(this.lastEvent)
    }
  }

  moveTooltip (event: PIXI.InteractionEvent) {
    this.lastEvent = event

    const newPosition = event.data.getLocalPosition(this.tooltipContainer)

    let xOffset = this.tooltipOffset - 20
    let yOffset = +40

    if (newPosition.x + xOffset < 0) {
      xOffset = CURSOR_WIDTH
    }

    if (newPosition.y + this.tooltip.height > HEIGHT) {
      yOffset = HEIGHT - newPosition.y - this.tooltip.height
    }

    this.tooltip.position.x = newPosition.x + xOffset
    this.tooltip.position.y = newPosition.y + yOffset

    const textBlocks = []
    for (const key of Object.keys(this.inside)) {
      const registryIdx = parseInt(key)
      const { getText } = this.registry[registryIdx]

      const text = getText()
      if (text != null && text.length > 0) {
        textBlocks.push(text)
      }
    }

    if (this.getGlobalText != null) {
      const text = this.getGlobalText(event.data)
      if (text != null && text.length > 0) {
        textBlocks.push(text)
      }
    }
    if (textBlocks.length > 0) {
      this.showTooltip(textBlocks.join('\n--------\n'))
    } else {
      this.hideTooltip()
    }
  }

  hideTooltip () {
    this.tooltip.visible = false
  }
}

function pointWithinBound (position: PIXI.IPoint, bounds: PIXI.Rectangle) {
  return position.x <= bounds.x + bounds.width &&
  position.y <= bounds.y + bounds.height &&
  position.x >= bounds.x &&
  position.y >= bounds.y
}
