package cn.adam.controller;

import io.github.lnyocly.ai4j.listener.SseListener;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletion;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletionResponse;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatMessage;
import io.github.lnyocly.ai4j.service.IChatService;
import io.github.lnyocly.ai4j.service.PlatformType;
import io.github.lnyocly.ai4j.service.factor.AiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ChatController {

    // 注入Ai服务
    @Resource
    private AiService aiService;

    @GetMapping("/deepseek/chat")
    public String getDeepSeekChatMessage(@RequestParam(value = "message", defaultValue = "Tell me a joke") String question) throws Exception {
        // 获取OpenAi的聊天服务
        IChatService chatService = aiService.getChatService(PlatformType.DEEPSEEK);

        // 创建请求参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("deepseek-chat")
                .message(ChatMessage.withUser(question))
                .build();

        System.out.println(chatCompletion);
        // 发送chat请求
        ChatCompletionResponse chatCompletionResponse = chatService.chatCompletion(chatCompletion);
        // 获取聊天内容和token消耗
        String content = chatCompletionResponse.getChoices().get(0).getMessage().getContent();
        long totalTokens = chatCompletionResponse.getUsage().getTotalTokens();
        System.out.println("总token消耗: " + totalTokens);

        return content;
    }

    @GetMapping("/ollama/chat")
    public String getOllamaChatMessage(@RequestParam(value = "message", defaultValue = "Tell me a joke") String question) throws Exception {
        // 获取OpenAi的聊天服务
        IChatService chatService = aiService.getChatService(PlatformType.OLLAMA);

        // 创建请求参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("qwen2.5:7b")
                .message(ChatMessage.withUser(question))
                .build();

        System.out.println(chatCompletion);
        // 发送chat请求
        ChatCompletionResponse chatCompletionResponse = chatService.chatCompletion(chatCompletion);
        // 获取聊天内容和token消耗
        String content = chatCompletionResponse.getChoices().get(0).getMessage().getContent();
        long totalTokens = chatCompletionResponse.getUsage().getTotalTokens();
        System.out.println("总token消耗: " + totalTokens);

        return content;
    }

    @GetMapping("/ollama/chat/stream")
    public String getOllamaChatMessageStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String question) throws Exception {
        // 获取OpenAi的聊天服务
        IChatService chatService = aiService.getChatService(PlatformType.OLLAMA);

        // 创建请求参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("qwen2.5:7b")
                .message(ChatMessage.withUser(question))
                .build();

        // 构造监听器
        SseListener sseListener = new SseListener() {
            @Override
            protected void send() {
                System.out.println("output" + this.getOutput());
            }
        };

        // 显示函数参数，默认不显示
        sseListener.setShowToolArgs(true);

        // 发送SSE请求
        chatService.chatCompletionStream(chatCompletion, sseListener);

        return sseListener.getOutput().toString();
    }


    @GetMapping("/deepseek/chat/stream")
    public String getChatMessageStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String question) throws Exception {
        // 获取OpenAi的聊天服务
        IChatService chatService = aiService.getChatService(PlatformType.DEEPSEEK);

        // 创建请求参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("deepseek-chat")
                .message(ChatMessage.withUser(question))
                .build();

        // 构造监听器
        SseListener sseListener = new SseListener() {
            @Override
            protected void send() {
                System.out.println(this.getCurrStr());
            }
        };

        // 显示函数参数，默认不显示
        sseListener.setShowToolArgs(true);

        // 发送SSE请求
        chatService.chatCompletionStream(chatCompletion, sseListener);

        return sseListener.getOutput().toString();
    }
}
