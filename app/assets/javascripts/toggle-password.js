function togglePassword(passwordField, toggle, event) {
    event.preventDefault()

    const doShow = passwordField.type === 'password'
    toggle.text = doShow ? 'Hide' : 'Show'
    passwordField.type = doShow ? 'text' : 'password'
}

document.querySelectorAll('[data-toggle-password]')
    .forEach(toggle => {
        const passwordField= document.getElementById(toggle.getAttribute("data-toggle-password"))
        toggle.addEventListener("click", event => togglePassword(passwordField, toggle, event))
    })
