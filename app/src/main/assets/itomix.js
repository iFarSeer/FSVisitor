function toast(message){
    itomix.invoke('toast.show', JSON.stringify({"message":message}));
}

function showAlertDialog(title, message){
    itomix.invoke('dialog.showAlertDialog', JSON.stringify({ "title": title, 'message': message }), 'success', 'failure');
}

function showConfirmDialog(title, confirm) {
    itomix.invoke('dialog.showConfirmDialog', JSON.stringify({'title':title, 'confirm':confirm}), 'success', 'failure');
}

function scan(){
    itomix.invoke('scanner.scan', JSON.stringify({}));
}

function print(){
    itomix.invoke('printer.print', JSON.stringify({}));
}


window.itomix = window.itomix || {};
window.itomix.invokeJS = function() {
    var completeName = Array.prototype.slice.call(arguments)[0]
    var args = Array.prototype.slice.call(arguments)[1]
    var result = completeName.concat('(', JSON.stringify(args), ')')
    toast(result)
//    var element = document.getElementById("result_content");
//    element.innerHTML = result;
}

