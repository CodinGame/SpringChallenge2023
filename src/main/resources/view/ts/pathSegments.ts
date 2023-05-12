import { AnimData, EventDto, AggregatedPathsEvent, PathSegment } from './types'
import { keyOf, last } from './utils.js'

interface PathSegmentKey {
  key: string
  from: number
  to: number
}

function * pathToKeys (path: number[]): Generator<PathSegmentKey> {
  let fromIdx = path[0]

  for (const toIdx of path.slice(1)) {
    const k = keyOf(fromIdx, toIdx)
    yield { key: k, from: fromIdx, to: toIdx }
    fromIdx = toIdx
  }
}

export function computePathSegments (events: EventDto[], playerIdx: number, type: number): AggregatedPathsEvent {
  if (events.length === 0) {
    return null
  }
  const segmentMap: Record<string, PathSegment> = {}
  let startAnim = Infinity
  let endAnim = -Infinity
  let total = 0
  const bouncing = []

  let totalMap = {}

  for (let event of events) {
    startAnim = Math.min(startAnim, event.animData.start)
    endAnim = Math.max(endAnim, event.animData.end)
    for (const { key, from, to } of pathToKeys(event.path)) {
      segmentMap[key] = segmentMap[key] ?? {
        pathKeys: new Set(),
        amount: 0,
        from,
        to,
        key
      }
      const segment = segmentMap[key]

      segment.amount += event.amount
      total += event.amount

      for (const { key } of pathToKeys(event.path)) {
        segment.pathKeys.add(key)
      }
    }
    const hillIdx = last(event.path)
    totalMap[hillIdx] = (totalMap[hillIdx] ?? 0) + event.amount
    bouncing.push(event.path[0])
  }

  return {
    animData: {
      start: startAnim,
      end: endAnim
    },
    segmentMap: segmentMap,
    segments: Object.values(segmentMap),
    totals: Object.entries<number>(totalMap).map(([k, v]) => ({ cellIdx: +k, amount: v })),
    bouncing,
    type,
    playerIdx
  }
}
