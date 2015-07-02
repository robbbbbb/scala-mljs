var points = [];
var clusters = null;

function clusterColor(idx) {
    var scale = 255 - (50 * Math.floor(idx / 3));
    function isPosition(idx, pos) { if (idx % 3 == pos) return 1; else return 0; }
    var r = isPosition(idx, 0);
    var g = isPosition(idx, 1);
    var b = isPosition(idx, 2);
    return 'rgb(' + (r*scale) + ',' + (g*scale) + ',' + (b*scale) + ')';
}

var chart = c3.generate({
    data: {
        xs: {
            points0: 'points0_x',
            points1: 'points1_x',
            points2: 'points2_x',
            centroid0: 'centroid0_x',
            centroid1: 'centroid1_x',
            centroid2: 'centroid2_x'
        },
        columns: [],
        colors: {
            points0: clusterColor(0),
            centroid0: clusterColor(0),
            points1: clusterColor(1),
            centroid1: clusterColor(1),
            points2: clusterColor(2),
            centroid2: clusterColor(2),
            points3: clusterColor(3),
            points4: clusterColor(4),
            points5: clusterColor(5)
        },
        type: 'scatter'
    },
    axis: {
        x: {
            label: 'X',
            tick: {
                fit: false
            }
        },
        y: {
            label: 'Y'
        }
    },
    transition: {
        duration: 0 // turn off animations so points don't animate as they move between clusters (which makes it look like the data is changing
    },
    size: {
        width: 640,
        height: 640
    }
});

function init() {
    var modality = 3;
    var varianceFromModalCenter = 10;
    function rand(pivot) { return pivot - varianceFromModalCenter + (Math.random() * varianceFromModalCenter * 2); }
    function randomPoint(modalCenter) { return new com.robminson.ml.Point(rand(modalCenter.x), rand(modalCenter.y)); }
    points = [];
    for (var mc = 0; mc < modality; mc++) {
        var modalCenter = new com.robminson.ml.Point(Math.random() * 100, Math.random() * 100);
        for (var i = 0; i < Math.floor(100 / modality); i++) {
            points = points.concat(randomPoint(modalCenter));
        }
    }
}
init();

$('#btn-reset').on('click', function (e) {
    init();
    clusters = com.robminson.ml.KMeansJSInterface().initialise(points, 3);
    updateClusters(clusters);
});

$('#btn-initialise').on('click', function (e) {
    clusters = com.robminson.ml.KMeansJSInterface().initialise(points, 3);
    updateClusters(clusters);
});

$('#btn-iterate').on('click', function (e) {
    clusters = com.robminson.ml.KMeansJSInterface().iterate(points, clusters, 3);
    updateClusters(clusters);
});

$('#btn-complete').on('click', function (e) {
    clusters = com.robminson.ml.KMeansJSInterface().calculate(points, 3);
    updateClusters(clusters);
});

$('#btn-reset-withdata').on('click', function (e) {
    // [{"x":1, "y":1}, {"x":99,"y":99}]
    var pointsJson = JSON.parse($('#text-pointdata').val())
    var fieldX = Object.keys(pointsJson[0])[0];
    var fieldY = Object.keys(pointsJson[0])[1];
    points = pointsJson.map(function(obj) { return new com.robminson.ml.Point(obj[fieldX], obj[fieldY]); });
    chart.axis.labels({x: fieldX, y: fieldY});
    clusters = com.robminson.ml.KMeansJSInterface().initialise(points, 3);
    updateClusters(clusters);
});

$('#btn-load-sessions-example').on('click', function (e) {
    $.getJSON("./sessions.json", "", function(sessions) { $('#text-pointdata').val(JSON.stringify(sessions)); })
});





function updateClusters(clusters) {
    for (var idx = 0; idx < clusters.length; idx++)
        loadOrUpdateCluster(clusters[idx], idx);
}

function loadOrUpdateCluster(cluster, clusterIdx) {
    var clusterName = 'points' + clusterIdx;
    var centroidName = 'centroid' + clusterIdx;
    chart.load({
        columns: [
            [clusterName + "_x"].concat(cluster.points.map(function(p) { return p.x; })),
            [clusterName].concat(cluster.points.map(function(p) { return p.y; })),
            [centroidName + "_x", cluster.centroid.x],
            [centroidName, cluster.centroid.y]
        ]
    });
    $(".c3-circles-" + centroidName).children().attr("r", "7.5").css("opacity", "1.0")
}