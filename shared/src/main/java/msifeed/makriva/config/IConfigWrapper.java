package msifeed.makriva.config;

public interface IConfigWrapper {
    ConfigData get();

    void selectShape(String name);

    void read();

    void write();
}
