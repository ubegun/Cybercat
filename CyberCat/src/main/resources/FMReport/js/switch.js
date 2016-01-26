function showElements(http_id, jst_id, menu_id) {
    for (i = 0; i < elenents.length; i++)
    {
        document.getElementById(elenents[i]).className = "row hidden";
    }
    for (i = 0; i < menu.length; i++)
    {
        document.getElementById(menu[i]).className = "notClass";
    }
    document.getElementById(menu_id).className = "active";
    document.getElementById(http_id).className = "row show";
    document.getElementById(jst_id).className = "row show";
}

