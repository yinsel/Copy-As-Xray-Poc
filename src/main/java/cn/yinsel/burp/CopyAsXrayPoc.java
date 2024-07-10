package cn.yinsel.burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import cn.yinsel.burp.data.POC;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CopyAsXrayPoc implements BurpExtension, ContextMenuItemsProvider, ClipboardOwner
{
    private MontoyaApi Api;
    @Override
    public void initialize(MontoyaApi api)
    {
        this.Api = api;
        api.extension().setName("CopyAsXrayPoc");
        api.userInterface().registerContextMenuItemsProvider(this);
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> menuItemList = new ArrayList<>();
        HttpRequest request = event.messageEditorRequestResponse().get().requestResponse().request();
        JMenuItem retrieveRequestItem = new JMenuItem("复制为 Xray Poc");
        retrieveRequestItem.addActionListener(e -> {
            try {
                copyMessage(request);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });
        menuItemList.add(retrieveRequestItem);
        return menuItemList;
    }

    private void copyMessage(HttpRequest request) throws JsonProcessingException {
        String method = request.method();
        List<HttpHeader> headers = request.headers();
        String body = request.bodyToString();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        POC poc = new POC();
        poc.rules.r0.request.method = method;
        poc.rules.r0.request.path = request.path();
        for (HttpHeader header : headers) {
            poc.rules.r0.request.headers.put(header.name(), header.value());
        }
        poc.rules.r0.request.body = Objects.equals(body, "") ? null : body;
        String pocYaml = mapper.writeValueAsString(poc);
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(pocYaml), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}