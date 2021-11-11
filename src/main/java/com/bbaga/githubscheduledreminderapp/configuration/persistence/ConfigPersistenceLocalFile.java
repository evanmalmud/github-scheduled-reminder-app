package com.bbaga.githubscheduledreminderapp.configuration.persistence;

import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigPersistenceLocalFile implements ConfigPersistenceInterface{

    private final String filePath;

    public ConfigPersistenceLocalFile(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public ConcurrentHashMap<String, ConfigGraphNode> load() {
        ConcurrentHashMap<String, ConfigGraphNode> graph = new ConcurrentHashMap<String, ConfigGraphNode>();
        File stateFile = new File(this.filePath);

        if (stateFile.exists() && stateFile.isFile()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stateFile.getAbsolutePath())))) {
                StringBuilder resultStringBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    resultStringBuilder.append(line).append("\n");
                }

                ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
                String stateJson = resultStringBuilder.toString();
                return objectMapper.readValue(stateJson, new TypeReference<ConcurrentHashMap<String, ConfigGraphNode>>() { });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return graph;
    }

    @Override
    public void dump(ConcurrentHashMap<String, ConfigGraphNode> config) {
        ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath))) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
