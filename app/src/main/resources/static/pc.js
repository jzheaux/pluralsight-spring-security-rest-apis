$( document ).ajaxSend((event, xhr) => {
    if (security.csrf.value) {
        xhr.setRequestHeader(security.csrf.header, security.csrf.value);
    }
});

$( document ).ajaxSuccess((event, xhr, objects, data) => {
    pc.up = () => pc._up(pc.root + "/up");
    $("#up-arrow").addClass("enabled")[0].onclick = pc.up;

    pc.down = () => pc._down(pc.root + "/down");
    $("#down-arrow").addClass("enabled")[0].onclick = pc.down;

    security.success(xhr);
});

const pc = {
    root: "http://127.0.0.1:8180/pc",
    read: () => $.ajax(pc.root,
            {
                method: 'GET',
                xhrFields: { withCredentials: true },
                success: (data) => $("#pc").html(data.pc)
            }),
    _up: (url) => $.ajax(url,
            {
                method: 'POST',
                xhrFields: { withCredentials: true },
                success: (data) => $("#pc").html(data.pc)
            }),
    _down: (url) => $.ajax(url,
            {
                method: 'POST',
                xhrFields: { withCredentials: true },
                success: (data) => $("#pc").html(data.pc)
            })
};

const security = {
    csrf: {
        header: "x-csrf-token"
    },
    success: (xhr) => {
        security.csrf.value = xhr.getResponseHeader(security.csrf.header);
    }
};

$(() => {
    pc.read();
});