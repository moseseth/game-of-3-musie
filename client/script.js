const socket = io("http://localhost:9092", {
	transports: ['websocket'],
	reconnection: false,
});

let currentPlayer = "Player-";
let gameStarted = false;

// Event listeners
document.getElementById("start-button").addEventListener("click", handleStartButtonClick);
document.getElementById("move-minus-one").addEventListener("click", () => handleManualPlayerMove("-1"));
document.getElementById("move-zero").addEventListener("click", () => handleManualPlayerMove("0"));
document.getElementById("move-plus-one").addEventListener("click", () => handleManualPlayerMove("1"));

// Socket event handlers
socket.on("connect", handleSocketConnect);
socket.on("random-number", handleRandomNumber);
socket.on("game-started", handleGameStarted);
socket.on("game-over", handleGameOver);
socket.on("game-state", handleGameState);
socket.on("player-move", handleManualPlayerMove);
socket.on("waiting-for-player-2", handleWaitingForPlayer2);
socket.on("game-full", handleGameFull);
socket.on('disconnect', (reason) => {
	console.log(`Disconnected: ${reason}`);
	socket.disconnect();
})

// Event handler functions
function handleStartButtonClick() {
	if (!gameStarted) {
		const currentPlayerId = socket.id;
		const autoMode = document.getElementById("toggle-switch").checked;
		socket.emit("start-game",  { playerId: currentPlayerId, autoMode: autoMode });
		gameStarted = true;

		if(!autoMode) {
			disableAutoModeToggle();
		}
	}
}

function disableAutoModeToggle() {
	let checkbox = document.getElementById("toggle-switch");
	checkbox.disabled = true;
}

function handleManualPlayerMove(move) {
	socket.emit("player-move", move);
}

function handleSocketConnect() {
	console.log("Connected to server");
	console.log(socket.id);
}

function handleRandomNumber(randomNumber) {
	const currentNumber = randomNumber.randomNumber;
	const currentPlayerId = randomNumber.id;

	const playerInfo = document.getElementById("player-info");
	playerInfo.innerHTML += `<p>${currentPlayer + currentPlayerId.substring(0, 4)}: ${currentNumber}</p>`;
	playerInfo.scrollTop = playerInfo.scrollHeight;

	currentPlayer = currentPlayer + currentPlayerId.substring(0, 4);

	disableStartButton();

	const autoMode = document.getElementById("toggle-switch").checked;
	if (autoMode) {
		disableAutoModeToggle();
		enableMoveButtons();
	}
}

function handleGameStarted(startingPlayerId) {
	const isStartingPlayer = startingPlayerId === socket.id;

	if (isStartingPlayer) {
		document.getElementById("turn-info").innerText = "Opponent's turn";
		disableMoveButtons();
	} else {
		document.getElementById("turn-info").innerText = "Your turn";
		enableMoveButtons();
	}
}

function handleGameOver(message) {
	document.getElementById("turn-info").innerText = "";
	document.getElementById("game-info").innerText = message;
	disableMoveButtons();
}

function handleGameState(gameState) {
	const currentNumber = gameState.playerPoint;
	const currentPlayerId = gameState.playerName;

	const playerInfo = document.getElementById("player-info");
	playerInfo.innerHTML += `<p>${currentPlayerId}: ${currentNumber}</p>`;
	playerInfo.scrollTop = playerInfo.scrollHeight;

	const turnInfo = document.getElementById("turn-info");
	turnInfo.innerText = (currentPlayerId === currentPlayer) ? "Opponent's turn" : "Your turn";

	if (currentNumber === 1) {
		turnInfo.innerText = "";
	}
}

function handleWaitingForPlayer2() {
	document.getElementById("game-info").innerText = "Waiting for Player 2 to join...";
}

function handleGameFull(message) {
	alert(message);
}

// Utility functions
function disableStartButton() {
	document.getElementById("start-button").disabled = true;
}

function enableStartButton() {
	document.getElementById("start-button").disabled = false;
}

function disableMoveButtons() {
	document.getElementById("move-minus-one").disabled = true;
	document.getElementById("move-zero").disabled = true;
	document.getElementById("move-plus-one").disabled = true;
}

function enableMoveButtons() {
	document.getElementById("move-minus-one").disabled = false;
	document.getElementById("move-zero").disabled = false;
	document.getElementById("move-plus-one").disabled = false;
}

document.getElementById('toggle-switch').addEventListener('change', function() {
	if (this.checked) {
		enableMoveButtons()
	} else {
		disableMoveButtons()
	}
});

