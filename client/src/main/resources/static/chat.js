const nodeAddress = document.getElementById("server-node-address").innerText
const wsUrlEndpoint = "/chat"

let socket = new WebSocket(`ws://${nodeAddress}${wsUrlEndpoint}`);

socket.onopen = function (_) {
    console.log("Connection established");
};

socket.onclose = function (event) {
    if (event.wasClean) {
        console.log(`Connection closed cleanly, code=${event.code} reason=${event.reason}`);
    } else {
        console.log('Connection died');
    }
};

socket.onerror = function (event) {
    console.log(`Error ${event.message}`);
};

socket.onmessage = function (event) {
    console.log(`Data received from server: ${event.data}`);
    document.getElementById("messages").innerText += event.data+"\n"
};

document.getElementById("send").onclick = function () {
    let content = document.getElementById("content").value;
    let message = {
        id: "",
        content: content,
        from: "",
        toList: [],
        timestamp: 0
    };
    let messageStr=JSON.stringify(message)
    socket.send(messageStr)
    console.log(`Message sent:  ${messageStr}`)
}
