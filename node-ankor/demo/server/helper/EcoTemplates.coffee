fs = require("fs")
{extend} = require("underscore")
eco = require("eco")
moment = require("moment")

#Setup eco template engine (express 3 incompatible by default)
#Also adds "inherit" and "include" functions and template caching

templates = {}
getTemplate = (path, basePath) ->
    if path.substr(path.length-4) != ".eco"
        path = "#{path}.eco"
    if path.indexOf(basePath) == -1
        path = "#{basePath}/#{path}"
    if not templates[path]
        templates[path] = fs.readFileSync(path, "utf8")
    return templates[path]

inherit = (path, content) ->
    template = getTemplate(path, @settings.views)
    return eco.render(template, extend(@, {
        inherited: content()
    }))

include = (path) ->
    template = getTemplate(path, @settings.views)
    return eco.render(template, @)
    
newlineToBr = (str) ->
    if not str
        return ""
    str = str.replace(/\r\n/g, "<br>")
    str = str.replace(/\r/g, "<br>")
    str = str.replace(/\n/g, "<br>")
    return str

module.exports = (path, context, cb) ->
    try
        template = getTemplate(path, context.settings.views)
        context = extend(context, {
            inherit: inherit,
            include: include,
            newlineToBr: newlineToBr,
            moment: moment
        })
        cb(null, eco.render(template, context).trim())
    catch error
        cb(error)
