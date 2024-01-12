const poundsCurrency = Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
})

function asMoney(number) {
    return poundsCurrency.format(number).replace('.00', '')
}

function calculateTotal(inputFields, totalElement) {
    const total = inputFields
        .map(inputField => Number(inputField.value))
        .map(num => isNaN(num) ? 0 : num)
        .reduce((a, b) => a + b, 0)
    totalElement.innerText = asMoney(total)
}

document.querySelectorAll('[data-total-items]')
    .forEach(totalElement => {
        const prefix = totalElement.getAttribute("data-total-prefix")
        const items = totalElement.getAttribute("data-total-items")
        const inputFields = items.split(',')
            .map(item => document.getElementById(prefix + item))

        inputFields.forEach(inputField => {
            inputField.addEventListener('keyup', _ => calculateTotal(inputFields, totalElement))
            inputField.addEventListener('blur', _ => calculateTotal(inputFields, totalElement))
        })
        calculateTotal(inputFields, totalElement)
    })
