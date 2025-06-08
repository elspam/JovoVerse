window.addEventListener('DOMContentLoaded', function () {
    const params = new URLSearchParams(window.location.search);

    // Tampilkan alert berdasarkan URL parameter
    if (params.get('success') === 'true') {
        document.getElementById('successAlert')?.classList.remove('d-none');
    }
    if (params.get('error') === 'userexists') {
        document.getElementById('userExistsAlert')?.classList.remove('d-none');
    }

    // Validasi form sebelum submit
    const form = document.querySelector('form');
    form.addEventListener('submit', function (e) {
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!username || !password) {
            e.preventDefault();
            document.getElementById('emptyFieldAlert')?.classList.remove('d-none');
        }
    });
});
