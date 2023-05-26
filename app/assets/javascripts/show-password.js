function togglePassword(passwordId, toggle, event) {
    event.preventDefault();

    const passwordField = document.getElementById(passwordId)
    const doShow = passwordField.type === 'password';
    toggle.text = doShow ? 'Hide' : 'Show';
    passwordField.type = doShow ? 'text' : 'password';
}
