$(function () {
    var n = 120,
        duration = 1000,
        now = new Date(Date.now() - duration)

    var margin_bottom = 20,
        width = 960,
        height = 600 - margin_bottom;

    var x = d3.time.scale()
        .domain([now - (n - 2) * duration, now - duration])
        .range([0, width]);

    var y = d3.scale.linear().range([height, 0]);

    var line = d3.svg.line()
        .interpolate("basis")
        .x(function(d, i) { return x(now - (n - 1 - i) * duration); })
        .y(function(d, i) { return y(d.v); });

    var svg = d3.select("#graphs").append("p").append("svg")
        .attr("width", width + margin_bottom)
        .attr("height", height + margin_bottom)

    svg.append("defs").append("clipPath")
        .attr("id", "clip")
        .append("rect")
        .attr("width", width)
        .attr("height", height);

    var axis = svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(x.axis = d3.svg.axis().scale(x).orient("bottom"));

    var path = svg.append("g")
        .attr("clip-path", "url(#clip)")
        .append("path")
        .attr("class", "line");

    function fetchData() {
        function onDataReceived(series) {
            //TODO, this should be dynamically selectable
            var data = series.response_time.sum.data;

            // update the domains
            now = new Date();
            x.domain([now - (n - 2) * duration, now - duration]);
            y.domain([0, d3.max(data, function(d) { return d.v })]);

            // redraw the line
            svg.select(".line")
                .datum(data)
                .attr("d", line)
                .attr("transform", null);

            // slide the x-axis left
            axis.transition()
                .duration(duration)
                .ease("linear")
                .call(x.axis);

            // slide the line left
            path.transition()
                .duration(duration)
                .ease("linear")
                .attr("transform", "translate(" + x(now - (n - 1) * duration) + ")")
        }

        $.ajax({
            url: "data.json",
            method: 'GET',
            dataType: 'json',
            success: onDataReceived
        });
    }
    setInterval(fetchData, duration);
});
