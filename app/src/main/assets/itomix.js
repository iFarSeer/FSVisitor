window.itomix = window.itomix || {};
window.itomix.invokeJS = function() {
    var completeName = Array.prototype.slice.call(arguments)[0]
    var args = Array.prototype.slice.call(arguments)[1]
    var result = completeName.concat('(', JSON.stringify(args), ')')
    eval(result)
}

function toast(message){
    itomix.invoke('toast.show', JSON.stringify({"message":message}));
}

function showAlertDialog(title, message, successCallback, failureCallback){
    itomix.invoke('dialog.showAlertDialog', JSON.stringify({ "title": title, 'message': message }), functionName(successCallback), functionName(failureCallback));
}

function showConfirmDialog(title, confirm, successCallback, failureCallback) {
    itomix.invoke('dialog.showConfirmDialog', JSON.stringify({ "title": title, 'confirm': confirm }), functionName(successCallback), functionName(failureCallback));
}

function scan(){
    itomix.invoke('scanner.scan', JSON.stringify({}));
}

function print(successCallback, failureCallback){
    itomix.invoke('printer.print', JSON.stringify({}), functionName(successCallback), functionName(failureCallback));
}

function onPrintStatusChanged(json) {
    toast(json)
}

function functionName(fun) {
  var ret = fun.toString();
  ret = ret.substr('function '.length);
  ret = ret.substr(0, ret.indexOf('('));
  return ret;
}

