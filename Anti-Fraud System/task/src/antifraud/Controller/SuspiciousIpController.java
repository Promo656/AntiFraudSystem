package antifraud.Controller;

import antifraud.Models.IP;
import antifraud.Models.ResponseOperationStatus;
import antifraud.Service.SuspiciousIpService;
import antifraud.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class SuspiciousIpController {
    @Autowired
    SuspiciousIpService suspiciousIpService;

    @PostMapping("/suspicious-ip")
    public ResponseEntity<IP> addIP(@Valid @RequestBody IP ip) {
        return suspiciousIpService.addIP(ip);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<ResponseOperationStatus> deleteIp(@Valid @PathVariable String ip) {
        return suspiciousIpService.deleteIp(ip);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<IP>> getAllIPs() {
        return suspiciousIpService.getAllIPs();
    }
}
