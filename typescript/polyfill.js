// Add print, printErr and readline functions

const { readline } = require('./readline.js')

// Polyfill missing SpiderMonkey functions:
global.print = console.log
global.printErr = console.warn
global.readline = readline
global.putstr = process.stdout.write
