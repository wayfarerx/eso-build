package net.wayfarerx.www.generator
package stylesheets

trait CommonCss {

  object CommonStyles {

    override def toString: String =
      s"""
         |  *, *:before, *:after {
         |    box-sizing: inherit;
         |  }
         |
         |  html {
         |    box-sizing: border-box;
         |  }
         |
         |  html, body {
         |    border: 0;
         |    margin: 0;
         |    padding: 0;
         |    max-width: 100%;
         |    overflow-x: hidden;
         |  }
         |
        |  body {
         |    background-color: $wxBackgroundColor;
         |    color: $wxTextColor;
         |    font: $wxCopyFont;
         |    font-size: 100%;
         |
        |    display: flex;
         |    flex-direction: column;
         |    justify-content: space-between;
         |  }
         |
        |  a { text-decoration: none; }
         |  a:link { color: $wxLinkColor; }
         |  a:visited { color: $wxVisitedColor; }
         |  a:hover { color: $wxHoverColor; }
         |  a:active { color: $wxActiveColor; }
         |""".stripMargin

  }

}
