module pdv.demo4 {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires java.desktop;

    exports pdv.demo4;
    exports pdv.demo4.model;
    exports pdv.demo4.view;
    exports pdv.demo4.controller;
    exports pdv.demo4.util;
}