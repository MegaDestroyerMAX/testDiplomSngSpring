document.getElementById('downloadBtn').addEventListener('click', () => {
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
        })
        .catch(err => alert(err.message));
});