document.getElementById("printLinkParagraph").classList.remove("govuk-!-display-none")
document.getElementById("printLinkParagraph").setAttribute("aria-hidden", "false")

document.getElementById("printLink").addEventListener("click", function (event) {
        event.preventDefault()
        window.print()
    }
)
