import http from 'k6/http'

/*  */
export default function () {

    const masterNode = 'node116'
    const fileName = 'hello-there-1671155741429-node117-354Z'

    // Obtain metadata
    const getMetadataUrl = `http://${masterNode}:2206/file/${fileName}/metadata`

    const servers = http.get(getMetadataUrl).json()
    console.log(`chunkserver: ${servers}`)

    // Read from chunkserver
    const getFileContentUrl = `http://${servers[0]}:2206/file/${fileName}/content`
    http.get(getFileContentUrl)
}