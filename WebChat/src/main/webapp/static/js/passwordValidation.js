function emptyNode(node) {
    while (node.firstChild) {
        node.removeChild(node.firstChild);
    }
}

function checkPassword(form) {
    var errorTextNode = document.getElementById("pwErrorText");
    emptyNode(errorTextNode);
    var password = form.password.value;
    var error = false;
    if (8 > password.length) {
        errorTextNode.appendChild(document.createTextNode(
                "The password must be at least 8 characters long.\n"));
        error = true;
    }

    var regex = /^(?=.*[a-z\u00e4\u00f6])(?=.*[A-Z\u00c4\u00d6])(?=.*[0-9_!%.,+-\\*?$€@^]).+$/;
    if (!regex.test(password)) {
        errorTextNode.appendChild(document.createTextNode(
                "The password must contain upper and lower case letters\n\
                        and at least one number or one of the following special \n\
                        characters: _!%.,+-*?$€@^"));
        error = true;
    }
    if (error) {
        event.preventDefault();
        return false;
    }
    return true;
}

