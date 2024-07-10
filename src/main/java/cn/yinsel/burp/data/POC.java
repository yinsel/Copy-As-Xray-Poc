package cn.yinsel.burp.data;
import burp.api.montoya.BurpExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POC {
    public String name = "poc";
    public String transport = "http";
    public Rules rules = new Rules();
    public String expression = "r0()";
    public Detail detail = new Detail();

    public static class Rules {
        public r0 r0 = new r0();

        public static class r0 {
            public Request request = new Request();
            public String expression = "response.status == 200";

            public static class Request {
                public String method = "";
                public String path = "";
                public Map<String, String> headers = new HashMap<>();
                public String body = "";
            }
        }
    }

    public static class Detail {
        public String author = "burp";
        public List<String> links = new ArrayList<>();
        public String description = "漏洞描述";

        {
            links.add("https://github.com/yinsel/Copy-As-Xray-Poc");
        }
    }
}
