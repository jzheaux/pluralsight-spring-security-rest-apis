$( document ).ajaxSuccess((event, xhr, objects, data) => {
    pc.up = () => pc._up(pc.root + "/up");
    $("#up-arrow").addClass("enabled")[0].onclick = pc.up;

    pc.down = () => pc._down(pc.root + "/down");
    $("#down-arrow").addClass("enabled")[0].onclick = pc.down;
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

$(() => {
    pc.read();
});