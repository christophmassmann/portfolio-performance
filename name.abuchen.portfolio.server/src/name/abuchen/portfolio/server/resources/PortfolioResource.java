package name.abuchen.portfolio.server.resources;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import name.abuchen.portfolio.json.JPerformanceIndexPoint;
import name.abuchen.portfolio.json.JPortfolio;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.Taxonomy;
import name.abuchen.portfolio.money.CurrencyConverterImpl;
import name.abuchen.portfolio.money.ExchangeRateProviderFactory;
import name.abuchen.portfolio.snapshot.PerformanceIndex;
import name.abuchen.portfolio.snapshot.PortfolioSnapshot;
import name.abuchen.portfolio.snapshot.filter.PortfolioClientFilter;
import name.abuchen.portfolio.ui.views.StatementOfAssetsViewer;
import name.abuchen.portfolio.util.Interval;

@Path("/portfolios")
public class PortfolioResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<JPortfolio> listPortfolios(@Context Client client)
    {
        var erFactory = new ExchangeRateProviderFactory(client);
        var cc = new CurrencyConverterImpl(erFactory, client.getBaseCurrency());
       
        var portfolios = client.getPortfolios().stream().map(portfolio -> {
            return JPortfolio.from(portfolio, PortfolioSnapshot.create(portfolio, cc, LocalDate.now()));
        });
        
        return portfolios.collect(Collectors.toList());
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/assets")
    public List<JAssetElement> getPortfolioSnapshot(
                    @PathParam("id") String id,
                    @QueryParam("from") LocalDate from,
                    @QueryParam("to") LocalDate to,
                    @QueryParam("groupBy") String groupBy,
                    @Context Client client) 
    {
        var oPortfolio = client.getPortfolios().stream().filter(portfolio -> portfolio.getUUID().equals(id)).findFirst();
        
        if(oPortfolio.isEmpty())
        {
            throw new NotFoundException();
        }
        
        var portfolio = oPortfolio.get();
        
        var erFactory = new ExchangeRateProviderFactory(client);
        var cc = new CurrencyConverterImpl(erFactory, client.getBaseCurrency());

        Taxonomy taxonomy = null;
        if(groupBy != null)
        {
             taxonomy = client.getTaxonomy(groupBy);
        }
        
        if (from == null)
            from = LocalDate.MIN;

        if (to == null)
            to = LocalDate.now();
        
        var interval = Interval.of(from, to);
        
        var model = new StatementOfAssetsViewer.Model(client, new PortfolioClientFilter(portfolio), cc, LocalDate.now(), taxonomy);
        model.calculatePerformanceAndInjectIntoElements(client.getBaseCurrency(), interval);
                
        var elements = model.getElements().stream().map(JAssetElement::from).collect(Collectors.toList(), interval);
        
        return elements;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/performance")
    public List<JPerformanceIndexPoint> getPerformance(
                    @PathParam("id") String id,
                    @QueryParam("from") LocalDate from,
                    @QueryParam("to") LocalDate to,
                    @Context Client client) 
    {
        var oPortfolio = client.getPortfolios().stream().filter(portfolio -> portfolio.getUUID().equals(id)).findFirst();
        
        if (oPortfolio.isEmpty())
        {
            throw new NotFoundException();
        }
        
        var erFactory = new ExchangeRateProviderFactory(client);
        var cc = new CurrencyConverterImpl(erFactory, client.getBaseCurrency());
        
        if (from == null)
            from = LocalDate.MIN;

        if (to == null)
            to = LocalDate.now();
        
        var interval = Interval.of(from, to);
        
        PerformanceIndex index = PerformanceIndex.forPortfolio(client, cc, oPortfolio.get(), interval, new ArrayList<Exception>());
        
        var result = new ArrayList<JPerformanceIndexPoint>();
        var dates = index.getDates();
        var values = index.getAccumulatedPercentage();
        for (int i = 0; i < dates.length; i++) {
            result.add(JPerformanceIndexPoint.from(dates[i], values[i]));
        }
        
        return result;
    }
}
