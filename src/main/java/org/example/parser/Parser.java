package org.example.parser;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

public interface Parser
{
    final WebClient webClient = initialiseWebClient();

    public static WebClient initialiseWebClient()
    {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setAjaxController(new AjaxController(){
            @Override
            public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
            { return true; }});
        return webClient;
    }

    public default HtmlPage getPageProductsListStore(String storeUrl, String xmlSeacrhInput, String xmlSearchButton, String productName) throws IOException
    {
        if(webClient == null)
        {
            return null;
        }

        HtmlPage pageFirst = webClient.getPage(storeUrl);

        webClient.waitForBackgroundJavaScript(10000);

        HtmlElement inputSearch = (HtmlElement) pageFirst.getFirstByXPath(xmlSeacrhInput);
        HtmlElement buttonSearch = (HtmlElement) pageFirst.getFirstByXPath(xmlSearchButton);
        buttonSearch.removeAttribute("disabled");

        inputSearch.setAttribute("value",productName);

        HtmlPage pageSecond = buttonSearch.click();
        webClient.waitForBackgroundJavaScript(1000);

        return pageSecond;
    }

    public List<Product> parsePages(HtmlPage pageProductsListStore, int countPage);

}