module.exports = {
    getCfg: function (environment) {
        switch (environment) {
            case "production": return prodCfg;
            default: return debugCfg;
        }
    }
};

const debugCfg = {
    frontendUrl: "http://localhost:3000",
    roomSrvUrl: "http://194.226.49.153:8888",
    port: 5000,
};

const prodCfg = {
    frontendUrl: "http://194.226.49.153:3000",
    roomSrvUrl: "http://194.226.49.153:8888",
    port: 5000,
};