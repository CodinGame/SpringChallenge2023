import { ViewModule, api } from './graphics/ViewModule.js'
import { EndScreenModule } from './endscreen-module/EndScreenModule.js';

// List of viewer modules that you want to use in your game
export const modules = [
  ViewModule,
  EndScreenModule
]

export const playerColors = [
  '#22a1e4', // curious blue
  '#ff1d5c' // radical red
]

export const options = [{
  title: 'DEBUG MODE',
  get: function () {
    return api.options.debugMode
  },
  set: function (value) {
    api.options.debugMode = value
    api.setDebug(value)
  },
  values: {
    'ON': true,
    'BLUE': 0,
    'RED': 1,
    'OFF': false
  }
},{
  title: 'ANTS',
  get: function () {
    return api.options.seeAnts
  },
  set: function (value) {
    api.options.seeAnts = value
    api.setSeeAnts(value)
  },
  values: {
    'NUMBERS': false,
    'PARTICLES': true
  }
}]


export const gameName = 'UTG2022'

export const stepByStepAnimateSpeed = 1
