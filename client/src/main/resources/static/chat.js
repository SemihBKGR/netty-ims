const proxyAddress = nettyIMSProxyAddress
const wsUrlEndpoint = "ws"

class message{
    constructor(id, content,from,toList,timestamp) {
        this.id = id
        this.content = content
        this.from = from
        this.toList = toList
        this.timestamp = timestamp
    }
}

let socket = new WebSocket(`ws://${proxyAddress}/${wsUrlEndpoint}`)

socket.onopen = function (_) {
    console.log("Connection established")
}

socket.onclose = function (event) {
    if (event.wasClean) {
        console.log(`Connection closed cleanly, code=${event.code} reason=${event.reason}`)
    } else {
        console.log('Connection died')
    }
}

socket.onerror = function (event) {
    console.log(`Error ${event.message}`)
}

socket.onmessage = function (event) {
    let msgStr = event.data
    let msg=JSON.parse(msgStr)
    console.log(`Message received: ${msgStr}`)
    let time = timeConverter(msg.timestamp)
    document.getElementById("messages").innerText += `${time} - ${msg.from} - ${msg.content}\n`
}

document.getElementById("send").onclick = function () {
    let content = document.getElementById("content").value
    let msg = new message("", content, "", [], 0)
    let msgStr=JSON.stringify(msg)
    socket.send(msgStr)
    console.log(`Message sent:  ${msgStr}`)
}

function timeConverter(timestamp){
    let date = new Date(timestamp);
    return date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds() ;
}
