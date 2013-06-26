http = require("http")
express = require("express")
{AnkorMan} = require("./AnkorMan")

ankorMan = new AnkorMan()
app = express()
app.set("port", 8080)

server = http.createServer(app)
server.listen(app.get("port"), ->
    ankorMan.init()
    console.log("Ankor demo is listening on port #{app.get("port")}")
)
