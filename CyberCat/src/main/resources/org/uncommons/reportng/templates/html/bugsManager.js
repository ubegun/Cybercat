$(document).ready(function() {
    $.getJSON("../TestArtifactIndex.json", function(data) {
        var tests = data.ArtifactIndex.tests;
        if (tests.length) {
            $.each(tests, function(index, test) {
                testBugsProcess(test);
            });
        } else {
            testBugsProcess(tests);
        }
    });

    var testBugsProcess = function(test) {
        var bugTest = $(".bugTest:contains('" + test.testGUID + "')");
        if (bugTest && bugTest.text()) {
            var bugs = test.bugs;
            if (bugs) {
                var bugElements = "";
                if (bugs.length) {
                    $.each(bugs, function(index, bug) {
                        bugElements += "<tr><td>" + bug.bugId + "</td><td>" + bug.bugSummary + "</td></tr>";
                    });
                } else {
                    bugElements += "<tr><td>" + bugs.bugId + "</td><td>" + bugs.bugSummary + "</td></tr>";
                }
                bugTest.after(bugElements);
            } else {
                $(".bugsTable").addClass("hidden");
            }
        }
    };
});
