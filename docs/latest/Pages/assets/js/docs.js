function trianglify() {
    var header = $('#jumbotron');

    var t = new Trianglify({
        cellsize: 90,
        noiseIntensity: 0,
        x_gradient: [window.colors[1], window.colors[0]]
    });

    var pattern = t.generate(window.screen.width | header.outerWidth(), header.outerHeight() * 1.2);

    header.css('background-image', pattern.dataUrl);
}

function geopattern() {
    var options = {};
    options.color = window.colors[0];
    options.baseColor = window.colors[1];

    if (window.selectedPattern) {
        options.generator = window.selectedPattern;
    }

    $('#jumbotron').geopattern(window.title, options);
}

function buildNav() {
    // var html = '';
    //
    // $('.bs-docs-section').each(function () {
    //     var h1 = $(this).find('h1[id]').first();
    //     var h23 = $(this).find('h2[id], h3[id], .panel[id]');
    //
    //     if (h1.length) {
    //         if (h1.data('name')) {
    //             html += '<li><a href="#' + h1[0].id + '">' + h1.data('name') + '</a>';
    //         }
    //         else {
    //             html += '<li><a href="#' + h1[0].id + '">' + h1.clone().children().remove().end().text() + '</a>';
    //         }
    //
    //         if (h23.length) {
    //             html += '<ul class="nav">';
    //             h23.each(function () {
    //                 if ($(this).data('name')) {
    //                     html += '<li><a href="#' + this.id + '">' + $(this).data('name') + '</a></li>';
    //                 }
    //                 else {
    //                     html += '<li><a href="#' + this.id + '">' + $(this).text() + '</a></li>';
    //                 }
    //             });
    //             html += '</ul>';
    //         }
    //
    //         html += '</li>';
    //     }
    // });
    //
    // if (html == '') {
    //     $('[role=complementary]').hide();
    //     $('[role=main]').toggleClass('col-md-9 col-md-12');
    // }
    // else {
    //     $('.bs-docs-sidenav').html(html);
    // }

    var $root = $('html, body');
    $('a').click(function () {
        var href = $.attr(this, 'href');

        if (href.startsWith('#')) {
            var target = $(href);

            if (target.length != 0) {
                $root.animate({
                    scrollTop: target.offset().top - 72
                }, 500, function () {
                    window.location.hash = href;
                });

                return false;
            }
            else {
                return true;
            }
        }
        else {
            return true;
        }
    });

    $('[data-toggle="tooltip"]').tooltip();

    /*!
     * JavaScript for Bootstrap's docs (http://getbootstrap.com)
     * Copyright 2011-2014 Twitter, Inc.
     * Licensed under the Creative Commons Attribution 3.0 Unported License. For
     * details, see http://creativecommons.org/licenses/by/3.0/.
     */
    !function (a) {
        a(function () {
            if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
                var b = document.createElement("style");
                b.appendChild(document.createTextNode("@-ms-viewport{width:auto!important}")), document.querySelector("head").appendChild(b)
            }
            {
                var c = a(window), d = a(document.body);
                a(".navbar").outerHeight(!0) + 10
            }
            d.scrollspy({target: ".bs-docs-sidebar", offset: 72}), c.on("load", function () {
                d.scrollspy("refresh")
            }), a(".bs-docs-container [href=#]").click(function (a) {
                a.preventDefault()
            }), setTimeout(function () {
                var b = a(".bs-docs-sidebar");
                b.affix({
                    offset: {
                        top: function () {
                            var c = b.offset().top, d = parseInt(b.children(0).css("margin-top"), 10), e = a(".bs-docs-nav").height();
                            return this.top = c - e - d
                        }, bottom: function () {
                            return this.bottom = a(".bs-docs-footer").outerHeight(!0)
                        }
                    }
                })
            }, 100), setTimeout(function () {
                a(".bs-top").affix()
            }, 100)
        })
    }(jQuery);
}
