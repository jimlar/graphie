$(function () {
    var options = {
        lines: { show: true },
        points: { show: true },
        xaxis: { tickDecimals: 0, tickSize: 1 }
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

        setTimeout(fetchData, 500);
    }
    fetchData();
});
