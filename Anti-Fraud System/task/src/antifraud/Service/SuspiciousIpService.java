package antifraud.Service;

import antifraud.Models.IP;
import antifraud.Models.Regex;
import antifraud.Models.ResponseOperationStatus;
import antifraud.Repositories.IPsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SuspiciousIpService {
    @Autowired
    IPsRepository iPsRepository;

    public ResponseEntity<IP> addIP(IP ip) {
        IP searchedIp = iPsRepository.findByIp(ip.getIp());
        if (searchedIp == null) {
            iPsRepository.save(ip);
            return new ResponseEntity<>(ip, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<ResponseOperationStatus> deleteIp(String ip) {
        if (!ip.matches(Regex.IP_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        IP searchedIp = iPsRepository.findByIp(ip);
        if (searchedIp != null) {
            String msg = String.format("IP %s successfully removed!", ip);
            iPsRepository.delete(searchedIp);
            return new ResponseEntity<>(new ResponseOperationStatus(msg), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<IP>> getAllIPs() {
        List<IP> ips = iPsRepository.findAll();
        return new ResponseEntity<>(ips, HttpStatus.OK);
    }
}
