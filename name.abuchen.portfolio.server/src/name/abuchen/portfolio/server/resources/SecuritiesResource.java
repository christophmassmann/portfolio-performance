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
import name.abuchen.portfolio.json.JSecurity;
import name.abuchen.portfolio.json.JSecurityPrice;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.money.CurrencyConverterImpl;
import name.abuchen.portfolio.money.ExchangeRateProviderFactory;
import name.abuchen.portfolio.snapshot.PerformanceIndex;
import name.abuchen.portfolio.util.Interval;

@Path("/securities")
public class SecuritiesResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<JSecurity> listSecurities(@Context Client client)
    {
        var securities = client.getSecurities().stream().map(JSecurity::from);


        return securities.collect(Collectors.toList());
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/prices")
    public List<JSecurityPrice> getPrices(
                    @PathParam("id") String id,
                    @QueryParam("from") LocalDate from,
                    @QueryParam("to") LocalDate to,
                    @Context Client client) 
    {
        var oSecurity = client.getSecurities().stream().filter(security -> security.getUUID().equals(id)).findFirst();
         
        if (oSecurity.isEmpty()) {
            throw new NotFoundException();
        }
        
        // @todo use from-to parameters
        
        var security = oSecurity.get();
        
        return security.getPrices().stream().map(JSecurityPrice::fromSecurityPrice).collect(Collectors.toList());
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
        var oSecurity = client.getSecurities().stream().filter(security -> security.getUUID().equals(id)).findFirst();
        if (oSecurity.isEmpty())
        {
            throw new NotFoundException();
        }
        

        if (from == null)
            from = LocalDate.MIN;

        if (to == null)
            to = LocalDate.now();
        
        var erFactory = new ExchangeRateProviderFactory(client);
        var cc = new CurrencyConverterImpl(erFactory, client.getBaseCurrency());

        var interval = Interval.of(from, to);
        
        PerformanceIndex index = PerformanceIndex.forInvestment(client, cc, oSecurity.get(), interval, new ArrayList<Exception>());
        
        var result = new ArrayList<JPerformanceIndexPoint>();
        var dates = index.getDates();
        var values = index.getAccumulatedPercentage();
        for (int i = 0; i < dates.length; i++) {
            result.add(JPerformanceIndexPoint.from(dates[i], values[i]));
        }
        
        return result;
    }
}
