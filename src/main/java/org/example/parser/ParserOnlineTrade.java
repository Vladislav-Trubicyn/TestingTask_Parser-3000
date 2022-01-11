package org.example.parser;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserOnlineTrade implements Parser
{

    @Override
    public HtmlPage getPageProductsListStore(String productName) throws IOException
    {
        if(webClient == null)
        {
            return null;
        }

        HtmlPage pageFirst = webClient.getPage("https://www.onlinetrade.ru");

        webClient.waitForBackgroundJavaScript(10000);

        HtmlElement inputSearch = (HtmlElement) pageFirst.getFirstByXPath("//input[@class='header__search__inputText js__header__search__inputText']");
        HtmlElement buttonSearch = (HtmlElement) pageFirst.getFirstByXPath("//input[@class='header__search__inputGogogo']");
        buttonSearch.removeAttribute("disabled");

        inputSearch.setAttribute("value",productName);

        HtmlPage pageSecond = buttonSearch.click();
        webClient.waitForBackgroundJavaScript(1000);

        return pageSecond;
    }

    @Override
    public List<Product> parsePages(HtmlPage pageProductsListStore, int countPage)
    {
        if(pageProductsListStore == null)
        {
            return null;
        }

        List<Product> listProduct = new ArrayList<Product>();
        int productsCount = 0;

        for(int page = 1; page <= countPage; page++)
        {
            if(page > 1)
            {
                HtmlElement nextPageButton = (HtmlElement) pageProductsListStore.getFirstByXPath("//a[@class='js__paginator__linkNext js__ajaxListingSelectPageLink js__ajaxExchange']");
                try
                {
                    pageProductsListStore = nextPageButton.click();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                webClient.waitForBackgroundJavaScript(5000);
            }

            productsCount = pageProductsListStore.getByXPath("//a[@class='indexGoods__item__name']").size();

            for(int i = 0; i < productsCount; i++)
            {
                listProduct.add(new Product(((HtmlElement)pageProductsListStore.getByXPath("//a[@class='indexGoods__item__name']").get(i)).getTextContent(),
                        ((HtmlElement)pageProductsListStore.getByXPath("//div[@class='indexGoods__item__price']/span").get(i)).getTextContent()));
            }
        }

        return listProduct;
    }
}
