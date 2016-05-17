# Unveiled-Server (Java-Stack)


## API Description
The following URLs can be used to interact with the Java-Stack of the Unveiled-Server:

| URL | Method | Paremeters | Description |
|-----|--------|------------|-------------|
|`{unveiled.base}/UploadFile` | POST | filename:String,<br/> suffix:String,<br/> author:Int(ID-reference),<br/> mediatype:String(MIME),<br/> latitude:Double,<br/> longitude:Double,<br/> public:Boolean,<br/> verified:Boolean | Is used to upload a file to the server. The content of the file must be send inside the HTTP requests body as bytes.|
||||
