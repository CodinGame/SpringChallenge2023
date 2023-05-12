
import { CellDto, EventDto, FrameDataDto, GlobalData, GlobalDataDto } from './types.js'

const MAIN_SEPARATOR = ';'

function splitLine (str) {
  return str.length === 0 ? [] : str.split(' ')
}

export function parseData (unsplit: string, globalData: GlobalData): FrameDataDto {
  const raw = unsplit.split(MAIN_SEPARATOR)

  let idx = 0
  const scores = [+raw[idx++], +raw[idx++]]
  const messages = [raw[idx++], raw[idx++]]
  const beacons = []
  for (let i = 0; i < globalData.playerCount; ++i) {
    beacons.push(splitLine(raw[idx++]).map(v => +v))
  }
  const events: EventDto[] = []
  const eventCount = +raw[idx++]
  for (let i = 0; i < eventCount; ++i) {
    const type = +raw[idx++]
    const start = +raw[idx++]
    const end = +raw[idx++]
    const playerIdx = +raw[idx++]
    const amount = +raw[idx++]
    const cellIdx = +raw[idx++]
    const targetIdx = +raw[idx++]
    const path = splitLine(raw[idx++]).map(v => +v)
    const animData = { start, end }

    events.push({
      playerIdx,
      amount,
      cellIdx,
      targetIdx,
      path,
      type,
      animData
    })
  }

  const parsed = {
    events,
    scores,
    messages,
    beacons
  }

  return parsed
}

export function parseGlobalData (unsplit: string): GlobalDataDto {
  const raw = unsplit.split(MAIN_SEPARATOR)
  let idx = 0

  const cells: CellDto[] = []
  const cellCount = +raw[idx++]
  for (let i = 0; i < cellCount; ++i) {
    const rawCell = splitLine(raw[idx++])
    const cell: CellDto = {
      index: i,
      q: +rawCell[0],
      r: +rawCell[1],
      richness: +rawCell[2],
      type: +rawCell[3],
      ants: [+rawCell[4], +rawCell[5]],
      owner: +rawCell[6]
    }
    cells.push(cell)
  }
  const parsed = {
    cells
  }
  return parsed
}
