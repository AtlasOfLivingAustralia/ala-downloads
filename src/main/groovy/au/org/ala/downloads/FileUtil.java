package au.org.ala.downloads;

import com.google.common.io.Closer;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

public class FileUtil {
    /**
     * Copy an inputstream to a file and get md5 and sha1 digests at the same time.
     *
     * @param file        The file to copy to
     * @param inputStream The input stream to copy from, it will be closed
     * @return a Map with keys 'md5' and 'sha' with the hex encoded versions of the given hash, and a length key
     * with the number of bytes in the input stream as a long
     */
     public static LinkedHashMap<String, Serializable> copyInputStreamToFileWithSha1AndMd5Digest(File file, InputStream inputStream) throws IOException {
        MessageDigest md5 = getDigest("MD5");
        MessageDigest sha = getDigest("SHA");
        long count;
        Closer closer = Closer.create();
        try {
            DigestInputStream md5Stream = closer.register(new DigestInputStream(inputStream, md5));
            DigestInputStream shaStream = closer.register(new DigestInputStream(md5Stream, sha));
            count = Files.copy(shaStream, file.toPath());
        } catch (Throwable e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }

        LinkedHashMap<String, Serializable> map = new LinkedHashMap<String, Serializable>(3);
        map.put("md5", Hex.encodeHexString(md5.digest()));
        map.put("sha", Hex.encodeHexString(sha.digest()));
        map.put("length", count);
        return map;
    }

    /**
     * Returns a <code>MessageDigest</code> for the given <code>algorithm</code>.
     *
     * @param algorithm the name of the algorithm requested. See <a
     *                  href="http://java.sun.com/j2se/1.3/docs/guide/security/CryptoSpec.html#AppA">Appendix A in the Java
     *                  Cryptography Architecture API Specification & Reference</a> for information about standard algorithm
     *                  names.
     * @return An MD5 digest instance.
     * @throws RuntimeException when a {@link java.security.NoSuchAlgorithmException} is caught.
     * @see java.security.MessageDigest#getInstance(String)
     */
    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
