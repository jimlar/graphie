$(function () {
    var options = {
        lines: {
            show: true,
            lineWidth: 2,
            align: "center"
        },
        points: { show: false },
        xaxis: {
            mode: "time",
            timeformat: "%H:%M %y-%0m-%0d"
        },
        grid: {
            hoverable: true
        }
    };

    function fetchData() {
        function onDataReceived(series) {
            var data = series;
            $.plot($("#placeholder"), data, options);
        }

        $.ajax({
            url: "data.json",
            method: 'GET',
            dataType: 'json',
            success: onDataReceived
        });

        setTimeout(fetchData, 2000);
    }
    fetchData();
});
