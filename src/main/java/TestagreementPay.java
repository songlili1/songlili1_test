import com.jd.fastjson.JSON;
import com.jd.jr.pay.export.Request;
import com.jd.jr.pay.export.Response;
import com.jd.jr.pay.export.pi.PayInstrument;
import com.jd.jr.pay.export.pi.ProxyCollAcctPi;
import com.jd.jr.pay.export.rest.PaySplit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;

public class TestagreementPay extends AbstractJavaSamplerClient {

    private static ClassPathXmlApplicationContext cxt = new ClassPathXmlApplicationContext("test-jsf.xml");
    private static final Log log = LogFactory.getLog(TestagreementPay.class);

    private static PaySplit paySplit = null;

    public Request request=new Request();
    public Response response = new Response();


    public SampleResult runTest(JavaSamplerContext arg0) {
        SampleResult sampleResult = new SampleResult();
        try{
            sampleResult.sampleStart();
            // 1.组织参数

            //request.setOrderId("20180928880000");
            request.setOrderId(arg0.getParameter("OrderId"));
            request.setPin("songlili1205");
            request.setDirective(0);
            //.......
            request.setPwdLevel("0");
            request.setIp("10.181.173.15");
            request.setClientType("jdpc");
            request.setOrderType("99999");
            request.setPayOrderAttr("9999");
            request.setGoodsType("00");
            request.setOrderSource("10");
            request.setBusinessSourceId("JD_SOPYPH");

            ProxyCollAcctPi proxyCollAcctPi = new ProxyCollAcctPi();
            proxyCollAcctPi.setAgencyCode("6029");
            proxyCollAcctPi.setReceivableAmount("100");
            proxyCollAcctPi.setTradeSubject("一盘货订单支付");
            proxyCollAcctPi.setTradeSource("jd");
            proxyCollAcctPi.setCustomerType("CUSTOMER_NO");
            proxyCollAcctPi.setCustomerNo("360080004002289679");

            //1.1列表参数创建
            ArrayList<PayInstrument> payInstruments=new ArrayList<PayInstrument>();
            //1.2创建列表里的一个对象
            payInstruments.add(proxyCollAcctPi);

            request.setPayInstruments(payInstruments);

            request.getExtMap().put("NO_SUBMIT_RISK","1");
            request.setQueryMarket(false);
            request.setOriPricePay(false);
            if(null == request.getExtMap()){
                request.setExtMap(new HashMap<String, String>());
            }

            // 2.调用接口
            response = paySplit.defaultPayWithoutExpressPi(request);


            // 3.处理返回结果
            if("0000".equals(response.getCode())){ // 成功
                log.info("ResCode="+ response.getInfo());
                sampleResult.setSuccessful(true);
                sampleResult.setResponseMessage(JSON.toJSONString(response));

            }else {
                log.info("ResCode="+response.getInfo());
                sampleResult.setSuccessful(false);
                sampleResult.setResponseMessage(JSON.toJSONString(response));
            }
        }catch (Exception e){
            log.error("TestError:"+e.getMessage());
            sampleResult.setSuccessful(false);
        }finally {
            sampleResult.sampleEnd();
        }
        return sampleResult;
    }


    public Arguments getDefaultParameters() {

        Arguments params = new Arguments();
        //举例params.addArgument("appId","KL0002");
        //params.addArgument("appId","normal");
        //params.addArgument("appToken","jd.repair.data.token");
        params.addArgument("OrderId","9302018000000");
        //params.addArgument("pin","songlili1205");
        //params.addArgument("TotalPrice","100.00");
        //params.addArgument("setMerchantId","50005");
        //params.addArgument("setOrderType","9999");

        return params;
    }

    public void setupTest(JavaSamplerContext context) {
        paySplit = (PaySplit) cxt.getBean("paySplit");
        super.setupTest(context);
    }


    public void teardownTest(JavaSamplerContext context) {
        super.teardownTest(context);
    }

}
