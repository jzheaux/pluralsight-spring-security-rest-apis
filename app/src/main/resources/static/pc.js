$( document ).ajaxSend((event, xhr) => {
    if (security.csrf.value) {
        xhr.setRequestHeader(security.csrf.header, security.csrf.value);
    }
    if (security.accessToken) {
        xhr.setRequestHeader("Authorization", "Bearer " + security.accessToken);
    }
});

$( document ).ajaxSuccess((event, xhr, objects, data) => {
    pc.up = () => pc._up(pc.root + "/up");
    $("#up-arrow").addClass("enabled")[0].onclick = pc.up;

    pc.down = () => pc._down(pc.root + "/down");
    $("#down-arrow").addClass("enabled")[0].onclick = pc.down;

    security.success(xhr);
});

$( document ).ajaxComplete((event, xhr) => {
    if (xhr.status === 401 || xhr.status === 403) {
        return security.authorize();
    }
});

const pc = {
    root: "http://127.0.0.1:8180/pc",
    read: () => $.get(pc.root, (data) => $("#pc").html(data.pc)),
    _up: (url) => $.post(url, (data) => $("#pc").html(data.pc)),
    _down: (url) => $.post(url, (data) => $("#pc").html(data.pc))
};

const security = {
    authorize: () => {
        return pkce.authorize();
    },
    token: (params) => {
        return pkce.token(params);
    },
    csrf: {
        header: "x-csrf-token"
    },
    success: (xhr) => {
        security.csrf.value = xhr.getResponseHeader(security.csrf.header);
    }
};

$(() => {
    const urlParams = new URLSearchParams(window.location.search);
    security.token(urlParams)
        .then((token) => {
            security.accessToken = token;
            pc.read();
        });
});