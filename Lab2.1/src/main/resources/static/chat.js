let stompClient = null;

window.addEventListener("load", function () {
  const nickname = sessionStorage.getItem("nickname");
  if (!nickname) {
    alert("Please enter a nickname first.");
    window.location.href = "index.html";
  } else {
    connect(nickname);
  }
});

function connect(nickname) {
  const socket = new SockJS("http://localhost:8081/chat");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function () {
    stompClient.subscribe("/topic/messages", function (message) {
      showMessage(JSON.parse(message.body));
    });
  });
}

function sendMessage() {
  const messageInput = document.getElementById("messageInput");
  const message = messageInput.value.trim();
  if (message && stompClient) {
    const nickname = sessionStorage.getItem("nickname");
    stompClient.send("/app/sendMessage", {}, JSON.stringify({ from: nickname, text: message }));
    messageInput.value = "";
  }
}

function showMessage(message) {
  const chatBox = document.getElementById("chatBox");
  const messageElement = document.createElement("div");
  messageElement.classList.add("message");
  messageElement.textContent = `${message.from}: ${message.text}`;
  chatBox.appendChild(messageElement);
  chatBox.scrollTop = chatBox.scrollHeight;
}
