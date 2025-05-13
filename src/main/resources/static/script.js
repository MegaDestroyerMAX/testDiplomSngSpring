// document.getElementById('downloadBtn').addEventListener('click', () => {
//     fetch('/api/export')
//         .then(response => {
//             if (!response.ok) throw new Error('Ошибка при скачивании файла');
//             return response.blob();
//         })
//         .then(blob => {
//             const url = window.URL.createObjectURL(blob);
//             const a = document.createElement('a');
//             a.href = url;
//             a.download = "employees.xlsx";
//             document.body.appendChild(a);
//             a.click();
//             a.remove();
//             window.URL.revokeObjectURL(url);
//         })
//         .catch(err => alert(err.message));
// });
function logMessage(msg) {
    const log = document.getElementById('log');
    const p = document.createElement('p');
    p.textContent = `[${new Date().toLocaleTimeString()}] ${msg}`;
    log.appendChild(p);
    console.log(msg);
}

function generateTable() {
    fetch('/api/export')
        .then(response => {
            if (!response.ok) throw new Error('Ошибка при скачивании файла');
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = "employees.xlsx";
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
            //logMessage("Таблица успешно сформирована и загружена.");
        })
        .catch(err => alert(err.message));
}


function connect() {
    const ip = document.querySelector('.ipInput').value;
    const code = document.querySelector('.replicaCodeInput').value;
    console.log("IP, CODE", ip, code)

    fetch('/api/connect', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ip, code })
    })
        .then(response => response.text())
        .then(msg => logMessage(msg))
        .catch(err => logMessage('Ошибка подключения: ' + err.message));
}
