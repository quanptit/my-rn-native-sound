export default class Sound {
    MAIN_BUNDLE: any;
    private _filename;
    private _loaded;
    private _key;
    private _duration;
    private _numberOfChannels;
    private _volume;
    private _pan;
    private _numberOfLoops;
    private _speed;
    /**Nếu truyền null basePath => Sẽ giữ nguyên fileName. thành địa chỉ của audio
     * callback(error, props)
     * basePath: RNSound.MAIN_BUNDLE => IOS là thư mục gốc của project
     * isStaticSound: Sử dụng cho bên ios. Sẽ dùng cái AVPlayer mặc định => sound không bị release khi play xong
     */
    constructor(filename: any, isStaticSound: any, callback: any, isFromResourceDir?: boolean);
    setLooping(isLooping: boolean): void;
    getFilePath(): any;
    isLoaded(): boolean;
    play(onEnd?: any): this;
    pause(): this;
    stop(): this;
    release(callbackComplete?: any): this;
    getDuration(): number;
    getNumberOfChannels(): number;
    getVolume(): number;
    setVolume(value: any): this;
    getPan(): number;
    setPan(value: any): this;
    getNumberOfLoops(): number;
    setNumberOfLoops(value: any): this;
    setSpeed(value: any): this;
    getCurrentTime(callback: any): void;
    setCurrentTime(value: number): this;
    setCategory(value: any, mixWithOthers?: boolean): void;
    enable(enabled: any): void;
    enableInSilenceMode(enabled: any): void;
}
