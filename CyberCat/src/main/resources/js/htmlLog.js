function tree_toggle(event) {
    event = event || window.event
    var clickedElem = event.target || event.srcElement

    if(hasClass(clickedElem, 'conf')){
    	configurationShowHide();
    	return
    }

    if (!hasClass(clickedElem, 'Expand')) {
        return // click in other place
    }
    // Node, which was clicked
    var node = clickedElem.parentNode
    if (hasClass(node, 'ExpandLeaf')) {
        return // click on Leaf
    }
    // new class for node
    var newClass = hasClass(node, 'ExpandOpen') ? 'ExpandClosed' : 'ExpandOpen'
    // switch current class to newClass
    // regex find open|close and change to newClass
    var re =  /(^|\s)(ExpandOpen|ExpandClosed)(\s|$)/
    node.className = node.className.replace(re, '$1'+newClass+'$3')
}

function hasClass(elem, className) {
    return new RegExp("(^|\\s)"+className+"(\\s|$)").test(elem.className)
}

function configurationShowHide(){
        var toReplaceClassNode= document.getElementById('toReplace');
    	var className = toReplaceClassNode.className;

    	//content- visible/invisible
    	var newClassName = hasClass(toReplaceClassNode, 'Opened') ? 'Closed' : 'Opened';
    	toReplaceClassNode.className = toReplaceClassNode.className.replace(className, newClassName);

        //replace symbol "+"/"-"
    	var spanNode = document.getElementsByClassName('conf')[0];
        var spanContent   = spanNode.textContent;
        var symbolToReplac = spanContent.trim().charAt(0)==='+' ? ' - ' : ' + ';
        var result = spanContent.replace(spanContent.trim().charAt(0), symbolToReplac);
        spanNode.innerHTML = result;
}
