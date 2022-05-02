let socket = new WebSocket("ws://localhost:9000");

socket.onopen = function(e) {
    alert("[open] Connection established");
};

socket.onmessage = function(event) {
    console.log(event)
    alert(`[message] Data received from server: ${event.data}`);
};

socket.onclose = function(event) {
    if (event.wasClean) {
        alert(`[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`);
    } else {
        alert('[close] Connection died');
    }
};

socket.onerror = function (error) {
    alert(`[error] ${error.message}`);
};

document.getElementById("message-send").onclick=function () {
    let content = document.getElementById("message-content").textContent;
    let message = {
        id: "1",
        content: content,
        from: "username",
        toList: [],
        timestamp: 1000000000
    };
    socket.send(JSON.stringify(message))
    alert(`message sent ${content}`)
}
