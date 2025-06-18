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
    console.log("Функция generateTable вызвана");
    fetch('/api/export')
        .then(response => {
            console.log("Ответ от сервера получен:", response);
            if (!response.ok) {
                return response.text().then(msg => {
                    console.log("Ошибка ответа:", msg); // Добавляем лог
                    showNotification("Ошибка", msg, "error");
                });
            }
            return response.blob();
        })
        .then(blob => {
            console.log("Blob получен");
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = "employees.xlsx";
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
            showNotification("Успех", "Таблица успешно сформирована и загружена.", "success");
        })
        .catch(err => {
            console.log("Ошибка поймана в catch:", err.message);
            showNotification("Ошибка", err.message, "error");
        });
}


function connect() {
    const ip = document.querySelector('.ipInput').value;
    const replicaCode = document.querySelector('.replicaCodeInput').value;

    if (!ip) {
        showNotification("Ошибка", 'Ip адрес не может быть пустым', 'error')
        return;
    }
    fetch('/db/connect', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'ip=' + encodeURIComponent(ip)
    })
        .then(response => response.text().then(text => {
            const data = JSON.parse(text);
            //console.log(data)
            const message = `
                Сообщение: ${data.message}
                Активные подключения: ${data.activeConnections}
            `;
            showNotification('Успех',message, 'success');
        }))
        .catch(error => {
            showNotification('Ошибка', 'Не удалось подключиться к серверу', 'error');
        });
}

function checkConnection(ip) {
    fetch(`db/connection/${encodeURIComponent(ip)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Ошибка при проверке соединения");
            }
            return response.json();
        })
        .then(data => {
            logMessage("Проверка соединения прошла успешно: " + JSON.stringify(data));
        })
        .catch(error => {
            showNotification('Ошибка', 'Проверка соединения не удалась', 'error');
            console.error(error);
        });
}

// Функция для показа уведомлений
function showNotification(title, message, type = 'info') {
    const background = {
        'success': '#28a745',
        'error': '#dc3545',
        'info': '#17a2b8',
        'warning': '#ffc107'
    }[type];

    Toastify({
        text: `${title}: ${message}`,
        duration: 5000,
        gravity: "bottom",
        position: "right",
        backgroundColor: background,
        stopOnFocus: true,
        className: "toast-notification"
    }).showToast();
}
