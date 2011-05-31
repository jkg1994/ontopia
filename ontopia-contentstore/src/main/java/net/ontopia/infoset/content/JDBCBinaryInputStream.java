
package net.ontopia.infoset.content;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * INTERNAL: 
 */

public class JDBCBinaryInputStream extends ContentInputStream {

  protected PreparedStatement ps;
  protected ResultSet rs;
  
  JDBCBinaryInputStream(PreparedStatement ps, ResultSet rs, InputStream stream, int length) {
    super(stream, length);
    this.ps = ps;
    this.rs = rs;
  }

  public int available() throws IOException {
    return stream.available();
  }

  public void close() throws IOException {
    try {
      stream.close();
    } finally {
      try {
        if (ps != null) {
          ps.close();
          ps = null;
        }
        if (rs != null) {
          rs.close();
          rs = null;
        }
      } catch (SQLException e) {
        throw new IOException(e.getMessage());
      }
    }
  }
  
  public void mark(int readlimit) {
    stream.mark(readlimit);
  }

  public boolean markSupported() {
    return stream.markSupported();
  }
  
  public int read() throws IOException {
    return stream.read();
  }
  
  public int read(byte[] b) throws IOException {
    return stream.read(b);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    return stream.read(b, off, len);
  }
  
  public void reset() throws IOException {
    stream.reset();
  }
  
  public long skip(long n) throws IOException {
    return stream.skip(n);
  }
  
}