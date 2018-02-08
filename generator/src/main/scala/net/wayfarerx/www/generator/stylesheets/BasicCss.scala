package net.wayfarerx.www.generator
package stylesheets

trait BasicCss {

  object BasicStyles {

    override def toString: String =
      s"""*, *:before, *:after {
         |  box-sizing: inherit;
         |}
         |
         |html {
         |  box-sizing: border-box;
         |}
         |
         |html, body {
         |  border: 0;
         |  margin: 0;
         |  padding: 0;
         |  max-width: 100%;
         |  overflow-x: hidden;
         |}
         |
         |body {
         |  background-color: $wxBackgroundColor;
         |  color: $wxTextColor;
         |  font: $wxCopyFont;
         |  font-size: 100%;
         |}
         |
         |body::after {
         |  display: block;
         |  position: fixed;
         |  top: 0;
         |  left: 0;
         |  height: 100vh;
         |  width: 100vw;
         |  z-index: -1;
         |  content: ' ';
         |
         |  background-attachment: fixed;
         |  background-position: center;
         |  background-repeat: no-repeat;
         |  background-size: cover;
         |}
         |
         |a { text-decoration: none; }
         |a:link { color: $wxLinkColor; }
         |a:visited { color: $wxVisitedColor; }
         |a:hover { color: $wxHoverColor; }
         |a:active { color: $wxActiveColor; }""".stripMargin

  }

}
