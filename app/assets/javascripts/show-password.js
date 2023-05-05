function togglePassword(passwordId, toggle, event) {
    event.stopPropagation();

    const passwordField = document.getElementById(passwordId)
    const doShow = passwordField.type === 'password';
    toggle.text = doShow ? 'Hide' : 'Show';
    passwordField.type = doShow ? 'text' : 'password';
}
