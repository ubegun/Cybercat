function initArtifact(jsonIndex) {

    var data = $.parseJSON(jsonIndex);

    $(document).ready(
            function() {
                $.each($('.testGUID > td'), function(index, item) {
                    $.each(data.ArtifactIndex.tests, function(key, val) {
                        $.each($('.testGUID > td'), function(index, item) {
                            var guid = $(item).text();
                            if (val.testGUID == guid) {
                                if (val.shortLog) {
                                    $('.shortLog').removeClass('hidden');
                                    $('.shortLog>a').attr('href', '../' + val.shortLog);
                                }
                                if (val.fullLog) {
                                    $('.fullLog').removeClass('hidden');
                                    $('.fullLog>a').attr('href', '../' + val.fullLog);
                                }

                                if (val.exceptionImage) {
                                    $('.exceptionImageWrapper').removeClass('hidden');
                                    $('.exceptionImage>td>a').attr('href', '../' + val.exceptionImage);
                                    $('.exceptionImage>td>a>img').attr('src', '../' + val.exceptionImage);
                                }

                                if (val.cookies) {
                                    $('.cookies').removeClass('hidden');
                                    $('.cookies>a').attr('href', '../' + val.cookies);
                                }

                                if (val.video) {
                                    $('.video').removeClass('hidden');
                                    $('.video>a').attr('href', '../' + val.video);
                                }

                                if (val.images) {
                                    $('.imagesLabel').removeClass('hidden');
                                    if ($.isArray(val.images) && val.images.length > 0) {
                                        $.each(val.images, function(index, value) {
                                            $('.imagesWrapper>td>div').append(
                                                    '<a target="_blank" href="../' + value + '"><img src="../' + value + '" class = "stepScreenShot"></a>');
                                        });
                                    } else {
                                        $('.imagesWrapper>td>div').append(
                                                '<a target="_blank" href="../' + val.images + '"><img src="../' + val.images + '" class = "stepScreenShot"></a>');
                                    }
                                }
                            }

                        });

                    });
                });

                $('.exceptionImageWrapper').click(function(event) {
                    $('.exceptionImage').toggle(800);
                });

                $('.imagesLabel').click(function(event) {
                    $('.imagesWrapper').toggle(800);
                });
                
            });

};